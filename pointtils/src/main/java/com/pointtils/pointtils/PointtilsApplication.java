package com.pointtils.pointtils;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PointtilsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PointtilsApplication.class, args);
	}

}
