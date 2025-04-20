package com.self.pft.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health Check")
public class HealthCheck {

    @Operation(summary = "Health Check")
    @GetMapping
    public String healthCheck(){
        return "application running.....";
    }

}
