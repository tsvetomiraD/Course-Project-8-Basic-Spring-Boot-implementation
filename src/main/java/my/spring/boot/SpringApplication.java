package my.spring.boot;

import dic.Container;
import jakarta.servlet.Servlet;
import my.spring.boot.annotations.*;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import rest.api.Role;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class SpringApplication {
    List<String> names = new ArrayList<>();
    Map<String, ControllerInfo> controllers = new HashMap<>();
    Map<String, ControllerInfo> specialControllers = new HashMap<>();
    static Container container;

    private SpringApplication(Class<?> primarySource, String[] args) throws Exception {
        container = new Container(new Properties());
        process(primarySource, args);
    }

    public void process(Class<?> primarySource, String[] args) throws Exception {
        Package pack = primarySource.getPackage();
        Set<Class<?>> allClasses = findAllClassesUsingClassLoader(pack.getName());

        for (String name : names) {
            allClasses.addAll(findAllClassesUsingClassLoader(name));
        }
        List<Class<?>> classes = allClasses.stream().filter(c -> c != null).toList();

        for (Class<?> clas : allClasses) {
            if (clas == null) {
                continue;
            }
            container.getInstance(clas);
        }

        for (Class<?> cl : allClasses) {
            if (cl == null) {
                continue;
            }
            if (cl.isAnnotationPresent(RestController.class)) {
                Class<?>[] interfaces = cl.getInterfaces();
                addController(cl, interfaces[0]);
                continue;
            }
            if (cl.isAnnotationPresent(Configuration.class)) {
                //System.out.println(cl);
            }
            if (cl.isAnnotationPresent(Mapper.class)) {
                System.out.println(container.getInstance(cl));
                //System.out.println(cl);
            }
        }

        //addFilters(ctx);
        start();
    }

    private void addController(Class<?> cl, Class<?> interf) throws Exception {
        //priemame che ima samo ein interface za daden controller
        Object instance = container.getInstance(cl);
        for (Field f : cl.getDeclaredFields()) {
            if (f.isAnnotationPresent(Autowired.class)) {
                Object field = container.getInstance(f.getType());
                f.setAccessible(true);
                f.set(instance, field);
            }
        }

        RequestMapping requestMapping = interf.getAnnotation(RequestMapping.class);
        String path = requestMapping.value();

        if (path == null || path.equals("/default")) {
            RequestMapping a = cl.getDeclaredAnnotation(RequestMapping.class);
            path = a.value();
        }

        for (Method method : cl.getDeclaredMethods()) {
            Annotation[] annotations = method.getAnnotations();
            if (annotations.length == 0) {
                Method m = interf.getMethod(method.getName(), method.getParameterTypes());
                annotations = m.getAnnotations();
            }
            Annotation anno = annotations[0];
            String value = String.valueOf(anno.annotationType().getMethod("value").invoke(anno));
            String requestMethod = null;
            String annoName = anno.annotationType().getSimpleName();

            switch (annoName) {
                case "GetMapping" -> requestMethod = "get";
                case "PostMapping" -> requestMethod = "post";
                case "PutMapping" -> requestMethod = "put";
                case "DeleteMapping" -> requestMethod = "delete";
            }

            if (annotations.length == 2) {
                anno = annotations[1];
                if (!anno.annotationType().equals(Role.class)) {
                    throw new Exception("Only one method annotation is allowed");
                }
                //todo check
            }
            ControllerInfo controllerInfo = new ControllerInfo(instance, method);
            String key = requestMethod + path + value;
            if (value.contains("{") && value.contains("}")) {
                specialControllers.put(key, controllerInfo);
                continue;
            }

            controllers.put(key, controllerInfo);
        }
    }

    private static void addFilters(Context ctx) {
        FilterDef filterDefAuth = new FilterDef();
        //filterDefAuth.setFilterClass(AuthenticationFilter.class.getName());
        filterDefAuth.setFilterName("Authentication");

        FilterDef filterDefLog = new FilterDef();
        //filterDefLog.setFilterClass(LoginFiler.class.getName());
        filterDefLog.setFilterName("Login");

        FilterMap filterMapAuth = new FilterMap();
        filterMapAuth.addServletName("PostServlet");
        filterMapAuth.addServletName("CommentServlet");
        filterMapAuth.setFilterName("Authentication");

        FilterMap filterMapLog = new FilterMap();
        filterMapLog.addServletName("PostServlet");
        filterMapLog.addServletName("CommentServlet");
        filterMapLog.addServletName("LoginServlet");
        filterMapLog.addServletName("RegisterServlet");
        filterMapLog.setFilterName("Authentication");

        ctx.addFilterDef(filterDefLog);
        ctx.addFilterMap(filterMapLog);

        ctx.addFilterDef(filterDefAuth);
        ctx.addFilterMap(filterMapAuth);
    }

    private <T> void start() throws Exception {
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(80);
        tomcat.getService().addConnector(connector);

        Context ctx = tomcat.addContext("/", null);
        Servlet servlet = new DispatcherServlet(controllers, specialControllers);
        tomcat.addServlet("/", "DispatcherServlet", servlet);

        ctx.addServletMappingDecoded("/*", "DispatcherServlet");

        tomcat.start();
        tomcat.getServer().await();
    }

    public static SpringApplication run(Class<?> primarySource, String[] args) { //todo change return type
        if (!primarySource.isAnnotationPresent(SpringBootApplication.class)) {
            throw new RuntimeException("Web application could not be started as there was no org.springframework.boot.web.servlet.server.ServletWebServerFactory bean defined in the context.");
        }
        try {
            return new SpringApplication(primarySource, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        return reader.lines()
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private Class<?> getClass(String className, String packageName) {
        if (className.endsWith(".class")) {
            try {
                return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
            } catch (ClassNotFoundException ignored) {
            } //todo chec
        }
        if (!className.equals("")) {
            String name = packageName + "." + className;
            names.add(name);
        }
        return null;
    }
}
