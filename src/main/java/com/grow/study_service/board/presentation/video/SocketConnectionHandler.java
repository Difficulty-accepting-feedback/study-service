package com.grow.study_service.board.presentation.video;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * WebSocket 연결 이벤트를 처리하는 핸들러 클래스.
 * <p>
 * 역할:
 * - 클라이언트가 특정 방(room)에 입장하거나 나가는 시점의 이벤트를 감지
 * - 현재 접속자 수를 관리하고 브로드캐스트 전송
 * - 방마다 연결된 사용자(member) 리스트를 Thread-safe하게 관리
 * </p>
 *
 * 동작 과정:
 * 1. 사용자가 웹소켓 연결 요청을 하면 {@link #handleSessionConnect(SessionConnectEvent)} 가 호출됨.
 * 2. 연결된 사용자를 방별 Set에 저장하고, 방 인원 수를 브로드캐스트함.
 * 3. 사용자가 연결을 끊으면 {@link #handleSessionDisconnect(SessionDisconnectEvent)} 가 호출되어,
 *    방 사용자 목록에서 제거하고 인원 수를 갱신함.
 */
@Slf4j
@Component
public class SocketConnectionHandler {

    /**
     * 방 ID → 해당 방에 접속한 사용자(memberId) Set.
     * ConcurrentHashMap을 사용하여 동시성 환경에서 안전하게 관리 가능.
     */
    @Getter
    private final Map<String, Set<String>> roomUsers;

    /** STOMP 메시지 전송을 위한 템플릿 */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 생성자 - WebSocket 메시징 템플릿 주입
     */
    public SocketConnectionHandler(SimpMessagingTemplate messagingTemplate) {
        roomUsers = new ConcurrentHashMap<>();
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 웹소켓 세션 연결 이벤트 처리
     *
     * @param event Spring이 publish하는 세션 연결 이벤트
     */
    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        log.info("[웹소켓 연결 확인] - 연결 시간: {}", LocalDateTime.now());

        // STOMP 헤더 추출
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        // 전체 헤더 내용 로깅
        log.info("전체 헤더 상세: {}", headers.getMessageHeaders());

        // custom header에서 roomId, memberId 추출
        String roomId = getHeaderValue(headers, "roomId");
        String memberId = getHeaderValue(headers, "memberId");

        // 해당 방의 사용자 Set을 가져오거나 새로 생성
        Set<String> users = roomUsers.computeIfAbsent(roomId, k -> new ConcurrentSkipListSet<>());

        // 사용자 Set에 새로운 사용자 추가
        boolean added = users.add(memberId);
        int count = users.size();

        log.info("방 사용자 업데이트: roomId={}, memberId={}, 추가 성공 여부={}, 총 인원={}",
                roomId, memberId, added, count);

        // 모든 클라이언트에 현재 방 인원 수 브로드캐스트
        broadCastForMemberCount(count, roomId);

        log.info("인원 수 업데이트 브로드캐스트 완료: roomId={}, count={}", roomId, count);
    }

    /**
     * 현재 방의 인원 수를 구독자들에게 브로드캐스트
     *
     * @param count  현재 방 인원 수
     * @param roomId 대상 방 ID
     */
    private void broadCastForMemberCount(int count, String roomId) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "memberCountUpdate");
        message.put("count", count);

        // /topic/room/{roomId} 경로로 구독 중인 모든 클라이언트에 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }

    /**
     * STOMP 헤더에서 특정 값 추출
     *
     * @param headers    STOMP 헤더 정보
     * @param headerName 추출할 헤더 키
     * @return 헤더에 저장된 값 (없을 시 null)
     */
    private String getHeaderValue(StompHeaderAccessor headers, String headerName) {
        String value = headers.getFirstNativeHeader(headerName);

        if (value != null) {
            log.info("헤더에 등록된 {} 추출 성공: {}", headerName, value);
        } else {
            log.warn("{} 가 헤더에 없음 - 헤더 확인 필요", headerName);
        }
        return value;
    }

    /**
     * 웹소켓 세션 연결 종료 이벤트 처리
     *
     * @param event Spring이 publish하는 세션 종료 이벤트
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        // TODO: 세션 종료 시 해당 사용자와 roomId 매핑 제거 로직 구현 필요
        // 예:
        // 1. 세션 ID → memberId/roomId 매핑을 저장한 separate map에서 lookup
        // 2. roomUsers Map에서 해당 memberId 제거
        // 3. 인원 수 업데이트 브로드캐스트
    }
}