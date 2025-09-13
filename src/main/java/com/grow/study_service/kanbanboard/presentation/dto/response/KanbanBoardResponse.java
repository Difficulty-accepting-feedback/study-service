package com.grow.study_service.kanbanboard.presentation.dto.response;

import com.grow.study_service.kanbanboard.domain.enums.KanbanStatus;
import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class KanbanBoardResponse {

    private String content; // 할 일 내용
    private KanbanStatus status; // 할 일 상태
    private LocalDateTime startDate; // 할 일 시작일
    private LocalDateTime endDate; // 할 일 종료일

    public static KanbanBoardResponse of(KanbanBoard kanbanBoard) {
        return new KanbanBoardResponse(
                kanbanBoard.getContent(),
                kanbanBoard.getStatus(),
                kanbanBoard.getStartDate(),
                kanbanBoard.getEndDate()
        );
    }
}