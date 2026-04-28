package com.logistics.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI logisticsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Logistics Order Management API")
                        .description("""
                                Backend service for managing logistics orders.
                                
                                **Status Lifecycle:** `CREATED` → `PICKED_UP` → `IN_TRANSIT` → `DELIVERED`
                                
                                Rules:
                                - No skipping steps
                                - No backward transitions
                                - Orders cannot be modified after DELIVERED
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Logistics Team")
                                .email("logistics@example.com"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")
                ));
    }
}
