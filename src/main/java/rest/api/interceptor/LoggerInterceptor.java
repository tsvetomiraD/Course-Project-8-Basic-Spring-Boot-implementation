package rest.api.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.spring.boot.interceptors.HandlerInterceptor;
import my.spring.boot.annotations.Component;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@Component
public class LoggerInterceptor implements HandlerInterceptor {
    //Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        //if (exception != null)
            //logger.error("Exception: {}", exception.getMessage());
    }
}
