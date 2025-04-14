package com.beanSpot.WEB3_4_Poten_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Web34PotenBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(Web34PotenBeApplication.class, args);

	}

}
