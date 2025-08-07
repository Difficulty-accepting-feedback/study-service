package com.grow.study_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication
public class StudyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyServiceApplication.class, args);
	}

}
