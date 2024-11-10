package com.RM.manageSystem.config;

import com.RM.manageSystem.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer { // 實現WebMvcConfigurer接口來自定義Spring MVC的配置

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://101.12.27.241:5173",
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "https://seanhuang110633.github.io"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加攔截器到Spring MVC攔截器鏈
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/user/register",
                        "/user/login"
                ); // 設置排除攔截的路徑，例如用戶註冊和登錄路徑不需要攔截
    }
}
