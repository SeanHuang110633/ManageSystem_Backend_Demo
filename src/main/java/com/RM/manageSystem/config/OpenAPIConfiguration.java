package com.RM.manageSystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * OpenAPI 配置類
 * 這個類用於配置 Swagger/OpenAPI 文檔，特別是設置 JWT 認證
 */
@Configuration
public class OpenAPIConfiguration {

    /**
     * 創建自定義的 OpenAPI bean
     * 這個 bean 將被 Spring 用來生成 API 文檔
     *
     * @return 配置好的 OpenAPI 對象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CustomerManageSystem API測試介面 ")
                        .version("1.0")
                        .description("""
        API 測試使用說明：

        1. 所有需要認證的 Api 都需要在請求頭(header)中添加 JWT Token。
        2. Token 格式：Bearer [Your-JWT-Token]
        3. 前端開發時，確保在 JWT Token 前手動加上 'Bearer ' 前綴。
        """)
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}

