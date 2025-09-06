package com.grow.study_service.group.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON 역직렬화 -> 빈 생성자 반드시 필요
@AllArgsConstructor
public class PaymentCompletedDto {

    private Long groupId; // 결제한 그룹 아이디 (멘토링 클래스의 ID)
    private Long memberId; // 결제한 멤버의 아이디
}