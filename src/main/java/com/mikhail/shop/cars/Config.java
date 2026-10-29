package com.mikhail.shop.cars;

import com.mikhail.shop.cars.interceptores.InterceptorAdmin;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class Config implements WebMvcConfigurer {

    private final InterceptorAdmin interceptorAdmin;

    public Config(InterceptorAdmin interceptorAdmin) {
        this.interceptorAdmin = interceptorAdmin;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptorAdmin);
    }
}