package com.grow.study_service.group.application.event;

import com.grow.study_service.group.domain.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentoringClassPurchaseRequestedEvent {

    private final Long groupId; // 결제 대상 ID
    private final Long memberId; // 결제할 멤버의 ID
    private final Category category; // 결제 대상 카테고리
    private final String groupName; // 결제 대상 이름
    private final int amount; // 결제 금액
}