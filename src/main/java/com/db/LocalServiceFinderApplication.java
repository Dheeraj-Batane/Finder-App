package com.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude={
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class LocalServiceFinderApplication {
	public static void main(String[] args) {
		SpringApplication.run(LocalServiceFinderApplication.class, args);
	}

}
