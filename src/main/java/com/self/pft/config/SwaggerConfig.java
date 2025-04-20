package com.self.pft.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomConfig(){
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Finance Tracker")
                        .description("By Sankalp")
        ).servers(List.of(
                        new Server().url("http://localhost:8080").description("local"),
                        new Server().url("http://localhost:8081").description("Live")
                ))
                .tags(List.of(
                        new Tag().name("Health Check")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .name("Authorization")
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                ;
    }
}
