package com.RM.manageSystem.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * ClassName: SwaggerConfig
 * PackageName: com.RM.manageSystem.config
 * Description: 允許 swagger測試介面繞過登入攔截
 *
 * @Create: 2024/9/14-下午 10:59
 */

@Component
public class SwaggerConfig {

    private static final List<String> SWAGGER_PATHS = Arrays.asList(
            "/swagger-ui.html",
            "/swagger-ui",
            "/webjars",
            "/v3/api-docs",
            "/swagger-resources"
    );

    public boolean isSwaggerRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SWAGGER_PATHS.stream().anyMatch(path::startsWith);
    }
}
