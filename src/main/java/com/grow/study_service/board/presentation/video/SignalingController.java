package com.grow.study_service.board.presentation.video;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
@RequiredArgsConstructor
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SocketConnectionHandler handler;

    /**
     * Offer/Answer 을 처리하는 메서드.
     * 클라이언트 A가 보낸 offer 혹은 answer 을 받아 대상 클라이언트 B에게 브로드캐스트합니다.
     * 클라이언트는 /app/room/{roomId}경로로 요청을 보냅니다.
     *
     * @param data 클라이언트가 보낸 offer / answer 데이터 (SDP 문자열).
     * @param roomId 채팅방의 고유 식별자 (roomId).
     * @return 받은 데이터를 그대로 반환하여 브로드캐스트.
     */
    /**
     * 전송되는 문자열 예시
     * {
     * "type":"offer",
     * "offer":{"sdp":"v=0\r\no=- 36648...,"type":"offer"}
     * }
     */
    @MessageMapping("/room/{roomId}")
    public void handleSignaling(@Payload Map<String, Object> message,
                                @DestinationVariable String roomId) {
        String type = (String) message.get("type");
        String senderId = (String) message.get("senderId");

        if (senderId == null) {
            log.warn("senderId가 없음 - 브로드캐스트 스킵");
            return;
        }

        // 방의 사용자 목록 가져오기
        Set<String> usersInRoom = handler
                .getRoomUsers()
                .getOrDefault(roomId, new HashSet<>());

        if (handler.getRoomUsers().isEmpty()) {
            log.warn("usersInRoom이 비어있음 - 브로드캐스트 스킵");
            // TODO 방이 비어있을 때 처리하기 (SimpUserRegistry 등을 활용)
        }

        // sender을 제외한 나머지 사용자들에게 offer 을 전송
        for (String targetId : usersInRoom) {
            if (!targetId.equals(senderId)) {
                messagingTemplate.convertAndSendToUser(targetId, "/queue/room/" + targetId, message);
                log.info("data 전송: type={}, from={}, to={}", type, senderId, targetId);
            }
        }
    }
}