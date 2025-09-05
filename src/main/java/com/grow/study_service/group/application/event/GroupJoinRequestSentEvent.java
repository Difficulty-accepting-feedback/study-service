package com.grow.study_service.group.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 그룹에 가입 요청이 전송된 후 발생되는 이벤트
@Getter
@AllArgsConstructor
public class GroupJoinRequestSentEvent {

    private final Long memberId; // 알림을 받는 사람의 아이디
    private final String message; // 알림 내용
    private final NotificationType notificationType; // 알림 타입 (그룹 참여 요청, 그룹 참여 승인, 그룹 참여 거절)
}