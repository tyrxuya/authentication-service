package com.tinqinacademy.authentication.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.tinqinacademy.authentication"})
@EnableJpaRepositories(basePackages = {"com.tinqinacademy.authentication.persistence.repositories"})
@EntityScan(basePackages = {"com.tinqinacademy.authentication.persistence.entities"})
@EnableScheduling
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }

}
