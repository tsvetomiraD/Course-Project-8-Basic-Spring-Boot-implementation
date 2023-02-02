package rest.api.config;

import my.spring.boot.interceptors.InterceptorRegistry;
import my.spring.boot.interceptors.WebMvcConfigurer;
import my.spring.boot.annotations.Autowired;
import my.spring.boot.annotations.Configuration;
import rest.api.interceptor.Interceptor;
import rest.api.interceptor.LoggerInterceptor;
@Configuration
public class ConfigurationClass implements WebMvcConfigurer {
    @Autowired
    LoggerInterceptor loggerInterceptor;
    @Autowired
    Interceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).excludePathPatterns("/manage/login", "/manage/register");
        registry.addInterceptor(loggerInterceptor);
    }
}
