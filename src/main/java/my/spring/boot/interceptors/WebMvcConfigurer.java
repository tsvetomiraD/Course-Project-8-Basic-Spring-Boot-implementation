package my.spring.boot.interceptors;

import my.spring.boot.interceptors.InterceptorRegistry;

public interface WebMvcConfigurer {
    public void addInterceptors(InterceptorRegistry registry);
}
