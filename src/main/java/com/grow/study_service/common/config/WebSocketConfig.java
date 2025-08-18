package com.grow.study_service.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * <h2>WebSocket 구성 클래스.</h2>
 * 이 클래스는 WebSocket 프로토콜을 통해 클라이언트와 서버 간 실시간 양방향 통신을 가능하게 하며,
 * pub-sub(발행-구독) 패턴을 지원하는 메시징 시스템을 구축합니다.
 * STOMP 프로토콜을 사용한 엔드포인트 등록과 메시지 브로커 설정을 담당합니다.
 *
 * @author sun
 * @version 1.0
 * @since 2025-08-07
 */
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 메시지 브로커를 구성하는 메서드.
     * 클라이언트가 구독할 토픽 경로와 서버로 메시지를 보낼 접두사를 설정합니다.
     *
     * @param config 메시지 브로커 레지스트리 인스턴스.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 클라이언트가 구독할 경로.
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트가 보낸 메시지를 처리하는 대상 경로.
    }

    /**
     * STOMP 엔드포인트를 등록하는 메서드.
     * 클라이언트가 WebSocket 연결을 시작할 수 있는 URL을 정의하며, 허용된 오리진을 설정합니다.
     *
     * @param registry STOMP 엔드포인트 레지스트리 인스턴스.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/signaling") // 웹소켓 연결 엔드포인트 URL
                .setAllowedOriginPatterns("*"); // 모든 도메인 허용
    }
}