package com.xuno.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${springdoc.api-version}")
    private String apiVersion;

    @Value("${springdoc.servers.development.url}")
    private String developmentUrl;

    @Value("${springdoc.servers.production.url}")
    private String productionUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("REST API for payment service operations")
                        .version(apiVersion)
                        .contact(new Contact()
                                .name("Payment Service Team")
                                .email("support@paymentservice.com")
                        ))
                .servers(List.of(
                        new Server()
                                .url(developmentUrl)
                                .description("Development Server"),
                        new Server()
                                .url(productionUrl)
                                .description("Production Server")
                ));
    }
}
