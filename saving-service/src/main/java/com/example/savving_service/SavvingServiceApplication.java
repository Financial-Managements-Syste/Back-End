package com.example.savving_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SavvingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SavvingServiceApplication.class, args);
	}

}
