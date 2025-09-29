package com.grow.study_service.dashboard.presentation.dto;

import com.grow.study_service.kanbanboard.presentation.dto.response.KanbanBoardResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DashboardResponseDto {
    private String pinnedNotice;  // 고정된 공지사항
    private Double attendanceRate;  // 출석률
    private Double progressPercentage;  // 진행률
    private List<KanbanBoardResponse> kanbanBoards;  // 칸반보드 목록

    @Builder
    public DashboardResponseDto(String pinnedNotice, Double attendanceRate, Double progressPercentage, List<KanbanBoardResponse> kanbanBoards) {
        this.pinnedNotice = pinnedNotice;
        this.attendanceRate = attendanceRate;
        this.progressPercentage = progressPercentage;
        this.kanbanBoards = kanbanBoards;
    }
}

