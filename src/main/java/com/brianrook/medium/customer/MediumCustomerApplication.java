package com.brianrook.medium.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class MediumCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediumCustomerApplication.class, args);
	}

}
