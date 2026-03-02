package com.autoflex.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        private static final String SECURITY_SCHEME_NAME = "bearerAuth";

        @Bean
        public OpenAPI autoflexOpenAPI() {
                return new OpenAPI()
                                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                                                new SecurityScheme()
                                                                .name(SECURITY_SCHEME_NAME)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")))
                                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
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