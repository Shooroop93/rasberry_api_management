package com.rasberry.rasberry_api_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties
@EnableScheduling
public class RasberryApiManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(RasberryApiManagementApplication.class, args);
    }
}