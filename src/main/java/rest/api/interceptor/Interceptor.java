package rest.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.spring.boot.interceptors.HandlerInterceptor;
import my.spring.boot.interceptors.HandlerMethod;
import my.spring.boot.annotations.Autowired;
import my.spring.boot.annotations.Component;
import rest.api.Role;
import rest.api.UserService;
import rest.api.model.MyUser;

@Component
public class Interceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        MyUser user = userService.validateUser(token);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return false;
        }

        HandlerMethod method = (HandlerMethod) handler;
        Role methodAnnotation = method.getMethodAnnotation(Role.class); //todo

        if (methodAnnotation == null) {
            return true;
        }

        String role = user.role;
        if (!role.equals(methodAnnotation.role())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return false;
        }

        return true;
    }
}
