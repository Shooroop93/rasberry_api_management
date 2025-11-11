package com.rasberry.rasberry_api_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties
public class RasberryApiManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(RasberryApiManagementApplication.class, args);
    }
}