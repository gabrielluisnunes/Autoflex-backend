package com.autoflex.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI autoflexOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Autoflex Inventory and Production API")
                        .description("REST API for products, raw materials, and production suggestions")
                        .version("v1")
                        .contact(new Contact()
                                .name("Autoflex Backend Team")
                                .email("backend@autoflex.local"))
                        .license(new License()
                                .name("Internal Technical Test")
                                .url("https://autoflex.local")));
    }
}