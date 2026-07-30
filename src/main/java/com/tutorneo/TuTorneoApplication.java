package com.tutorneo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TuTorneoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TuTorneoApplication.class, args);
	}

}
