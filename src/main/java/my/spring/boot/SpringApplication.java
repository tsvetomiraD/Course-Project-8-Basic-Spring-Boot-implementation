package my.spring.boot;

import dic.Container;
import jakarta.servlet.http.HttpServlet;
import my.spring.boot.annotations.Autowired;
import my.spring.boot.annotations.Configuration;
import my.spring.boot.annotations.RequestMapping;
import my.spring.boot.annotations.RestController;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import rest.api.controller.CommentController;
import rest.api.controller.PostController;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SpringApplication {
    List<String> names = new ArrayList<>();
    Map<Class, String> classToPath = new HashMap<>();
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
        Tomcat tomcat = setTomcat();

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

        for (Class<?> cl : allClasses) {
            if (cl == null)
                continue;

            if (cl.isAnnotationPresent(RestController.class)) {
                Class<?>[] interfaces = cl.getInterfaces();
                addServlets(ctx, cl, interfaces);
            }
            if (cl.isAnnotationPresent(Configuration.class)) {
                //System.out.println(cl);
            }
        }


        //addFilters(ctx);

        //tomcat.start();
        //tomcat.getServer().await();
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

    private static <T> void addServlets(Context ctx, Class<?> cl, Class<?>[] interfaces) throws Exception {
        HttpServlet servlet = new HttpServletSetter();
        Object instance = container.getInstance(cl);
        for (Field f : cl.getDeclaredFields()) {
            if (f.isAnnotationPresent(Autowired.class)) {
                Object field = container.getInstance(f.getType());
                f.setAccessible(true);
                f.set(instance, field);
                System.out.println(f.get(instance));
            }
        }
        Tomcat.addServlet(ctx, cl.getName(), servlet);
        String path = null;
        for (Class<?> o : interfaces) {
            if (path == null) {
                RequestMapping a = o.getAnnotation(RequestMapping.class);
                path = a.value();
            }
        }
        if (path == null || path.equals("/default")) {
            RequestMapping a = cl.getDeclaredAnnotation(RequestMapping.class);
            path = a.value();
        } //todo set path default to / and fix this checks

        ctx.addServletMappingDecoded(path + "/*", cl.getName());
    }

    private static Tomcat setTomcat() {
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(80);
        tomcat.getService().addConnector(connector);

        return tomcat;
    }

    public static SpringApplication run(Class<?> primarySource, String[] args) { //todo change return type
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
