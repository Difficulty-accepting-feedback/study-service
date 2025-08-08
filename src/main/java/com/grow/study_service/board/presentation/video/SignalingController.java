package com.grow.study_service.board.presentation.video;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * <h2>WebRTC 시그널링 컨트롤러 클래스.</h2>
 * 이 클래스는 클라이언트 간 offer와 answer를 처리하여 P2P 연결을 위한 시그널링을 담당합니다.
 * Spring WebSocket을 사용하며, 특정 토픽으로 메시지를 브로드캐스트합니다.
 * 로그를 통해 요청 수신을 기록하여 디버깅을 용이하게 합니다.
 *
 * @author sun
 * @version 1.0
 * @since 2025-08-07
 */
@Slf4j
@Controller
public class SignalingController {

    /**
     * Offer를 처리하는 메서드.
     * 클라이언트 A가 보낸 offer를 받아 대상 클라이언트 B의 토픽으로 브로드캐스트합니다.
     * 클라이언트는 /app/offer/{roomId}/{targetMemberId} 경로로 요청을 보냅니다.
     *
     * @param offer 클라이언트가 보낸 offer 데이터 (SDP 문자열).
     * @param roomId 채팅방의 고유 식별자 (roomId).
     * @param targetMemberId 대상 회원의 고유 식별자 (targetMemberId).
     * @return 받은 offer 데이터를 그대로 반환하여 브로드캐스트.
     */
    /**
     * 전송되는 문자열 예시
     * {
     *   "type": "offer",
     *   "sdp": "v=0\r\no=- 6137031273746274589 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=group:BUNDLE (이하 생략)"
     * }
     */
    @MessageMapping("/offer/{roomId}/{targetMemberId}")
    @SendTo("/topic/peer/offer/{roomId}/{targetMemberId}")
    public String handleOffer(@Payload String offer,
                              @DestinationVariable String roomId,
                              @DestinationVariable String targetMemberId) {
        log.info("[Offer Received] 요청 수신 완료 | roomId={}, targetMemberId={}, offer=[{}]",
                roomId, targetMemberId, offer);
        return offer;
    }

    /**
     * Answer를 처리하는 메서드.
     * 클라이언트 B가 보낸 answer를 받아 대상 클라이언트 A의 토픽으로 브로드캐스트합니다.
     * 클라이언트는 /app/peer/answer/{roomId}/{targetMemberId} 경로로 요청을 보냅니다.
     *
     * @param answer 클라이언트가 보낸 answer 데이터 (SDP 문자열).
     * @param roomId 채팅방의 고유 식별자 (roomId).
     * @param targetMemberId 대상 회원의 고유 식별자 (targetMemberId).
     * @return 받은 answer 데이터를 그대로 반환하여 브로드캐스트.
     */
    @MessageMapping("/peer/answer/{roomId}/{targetMemberId}")
    @SendTo("/topic/peer/answer/{roomId}/{targetMemberId}")
    public String handleAnswer(@Payload String answer,
                               @DestinationVariable String roomId,
                               @DestinationVariable String targetMemberId) {
        log.info("[Answer Received] 요청 수신 완료 | roomId={}, targetMemberId={}, answer=[{}]",
                roomId, targetMemberId, answer);
        return answer;
    }
}