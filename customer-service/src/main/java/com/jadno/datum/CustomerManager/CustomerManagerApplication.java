package com.jadno.datum.CustomerManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CustomerManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerManagerApplication.class, args);
    }
}
