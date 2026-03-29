package com.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude={
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@EnableAsync
public class LocalServiceFinderApplication {
	public static void main(String[] args) {
		SpringApplication.run(LocalServiceFinderApplication.class, args);
	}

}
