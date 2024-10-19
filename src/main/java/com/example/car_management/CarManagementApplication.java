package com.example.car_management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "Library APIs", version = "1.0", description = "Library Management APIs."))
public class CarManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarManagementApplication.class, args);
    }
}