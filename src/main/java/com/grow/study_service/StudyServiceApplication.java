package com.grow.study_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@EnableWebSocket
@EnableScheduling
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커를 활성화
@SpringBootApplication
public class StudyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyServiceApplication.class, args);
	}

}
