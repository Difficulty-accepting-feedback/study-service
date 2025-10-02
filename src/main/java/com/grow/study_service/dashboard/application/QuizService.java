package com.grow.study_service.dashboard.application;

import com.grow.study_service.dashboard.presentation.dto.QuizRankDto;

import java.util.List;

public interface QuizService {
    List<QuizRankDto> getQuizRanking(Long groupId, Long memberId);
}
