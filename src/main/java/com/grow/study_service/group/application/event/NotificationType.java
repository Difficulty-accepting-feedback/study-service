package com.grow.study_service.group.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    GROUP_JOIN_REQUEST("[그룹 참여 요청]"),
    GROUP_JOIN_APPROVAL("[그룹 참여 승인]"),
    GROUP_JOIN_REJECTION("[그룹 참여 거절]");

    private final String description;
}
