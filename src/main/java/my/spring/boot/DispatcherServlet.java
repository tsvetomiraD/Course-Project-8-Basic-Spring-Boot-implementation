package my.spring.boot;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.spring.boot.annotations.PathVariable;
import my.spring.boot.annotations.RequestBody;

import javax.crypto.spec.OAEPParameterSpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet implements Servlet {
    Map<String, ControllerInfo> controllers;
    Map<String, ControllerInfo> specialControllers;

    public DispatcherServlet(Map<String, ControllerInfo> controllers, Map<String, ControllerInfo> specialControllers) {
        this.controllers = controllers;
        this.specialControllers = specialControllers;
    }
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String path = request.getPathInfo();
        String requestMethod = request.getMethod().toLowerCase();
        String key = requestMethod + path;

        ControllerInfo controllerInfo = controllers.get(key);
        if (controllerInfo == null) {
            controllerInfo = specialControllers.get(key);
            //todo checks with regex
        }
        Method method = controllerInfo.method;
        List<Object> args = getMethodParams(request, method);
        writeResponse(response, controllerInfo, args);
    }

    private static void writeResponse(HttpServletResponse response, ControllerInfo controllerInfo, List<Object> args) throws IOException {
        Object res;
        try {
            res = controllerInfo.invoke(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(res);
        out.flush();
    }

    private static List<Object> getMethodParams(HttpServletRequest request, Method method) throws IOException {
        List<Object> args = new ArrayList<>();
        Parameter[] params = method.getParameters();

        for (Parameter param : params) {
            Object p = null;
            if (param.isAnnotationPresent(RequestBody.class)) {
               BufferedReader reader = request.getReader();
               StringBuilder res = new StringBuilder();
               String line = reader.readLine();
               while (line != null) {
                   res.append(line);
                   line = reader.readLine();
               }
               p = new Gson().fromJson(res.toString(), param.getType());
            } else if (param.isAnnotationPresent(PathVariable.class)) {
                String paramName = param.getName();
                param = null; //todo add param from path create regex for matching
            }
            args.add(p);
        }
        return args;
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
