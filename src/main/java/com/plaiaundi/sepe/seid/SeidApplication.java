package com.plaiaundi.sepe.seid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.plaiaundi.sepe.seid"})
public class SeidApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeidApplication.class, args);
	}

}
