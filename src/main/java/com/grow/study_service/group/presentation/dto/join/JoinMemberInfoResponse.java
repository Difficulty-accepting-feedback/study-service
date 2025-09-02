package com.grow.study_service.group.presentation.dto.join;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinMemberInfoResponse {

    private int score; // 신뢰도 점수
    private String nickname; // 닉네임
}