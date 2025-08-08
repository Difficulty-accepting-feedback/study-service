package com.grow.study_service.board.presentation.video;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class TestStompController {

    // 간단한 STOMP 메시지 수신 테스트: 클라이언트가 "/app/test"로 메시지 보내면 로그 찍고 브로드캐스트
    @MessageMapping("/test")  // 클라이언트가 "/app/test"로 send하면 이 메서드 호출
    @SendTo("/topic/test")    // 응답을 "/topic/test" 토픽으로 브로드캐스트 (구독한 클라이언트가 받음)
    public String test(@Payload String message) {
        log.info("[STOMP Test] 메시지 수신 완료: {}", message);  // 전송 여부 확인을 위한 로그
        return "테스트 응답: " + message;  // 받은 메시지를 반환
    }
}