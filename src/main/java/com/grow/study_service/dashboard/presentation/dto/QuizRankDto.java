package com.grow.study_service.dashboard.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizRankDto {

    private int Ranking; // 순위
    private String nickname; // 닉네임
    private int score; // 점수
}
