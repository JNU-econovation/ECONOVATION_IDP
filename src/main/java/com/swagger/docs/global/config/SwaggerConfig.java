package com.swagger.docs.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig{

    @Bean
    public GroupedOpenApi AccountApi() {
        return GroupedOpenApi.builder()
                .group("AccountController")
                .packagesToScan("com.swagger.docs.controller")
                .pathsToExclude("/api/user/**")
                .pathsToMatch("/api/account/**")
                .build();
    }

    @Bean
    public GroupedOpenApi UserApi() {
        return GroupedOpenApi.builder()
                .group("UserController")
                .packagesToScan("com.swagger.docs.controller")
                .pathsToExclude("/api/account/**")
                .pathsToMatch("/api/user/**")
                .build();
    }
    @Bean
    public OpenAPI customOpenAPI() {
        License license = new License().name("Copyright(C) CWY Corporation All rights reserved.");
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Spring Boot API Example")
                        .description("Econovation 통합 유저 서버")
                        .version("v1.0.0"));
    }
}
