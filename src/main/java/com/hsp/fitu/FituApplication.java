package com.hsp.fitu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FituApplication {

	public static void main(String[] args) {
		SpringApplication.run(FituApplication.class, args);
	}

}
