package my.spring.boot;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.spring.boot.annotations.PathVariable;
import my.spring.boot.annotations.RequestBody;

import javax.crypto.spec.OAEPParameterSpec;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet implements Servlet {
    Map<String, ControllerInfo> controllers = new HashMap<>();
    Map<String, ControllerInfo> specialControllers = new HashMap<>();

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
        Method method = controllerInfo.method;
        Parameter[] params = method.getParameters();
        List<Object> args = new ArrayList<>();
        for (Parameter param : params) {
            if (param.isAnnotationPresent(RequestBody.class)) {
                String paramName = param.getName();
                request.getInputStream();
            } else if (param.isAnnotationPresent(PathVariable.class)) {
                String paramName = param.getName();
                param = null; //add param from path create regex for matching

            }
            args.add(param);
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
