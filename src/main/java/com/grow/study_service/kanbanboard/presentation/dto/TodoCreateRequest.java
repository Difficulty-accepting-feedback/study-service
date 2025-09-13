package com.grow.study_service.kanbanboard.presentation.dto;

import com.grow.study_service.kanbanboard.domain.enums.KanbanStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TodoCreateRequest {

    @NotBlank(message = "내용은 비어 있을 수 없습니다.")
    private final String content;

    @NotNull(message = "상태는 null일 수 없습니다.")
    private final KanbanStatus status;

    @NotNull(message = "시작 날짜는 null일 수 없습니다.")
    private final LocalDateTime startDate;

    @NotNull(message = "종료 날짜는 null일 수 없습니다.")
    @Future(message = "종료 날짜는 미래여야 합니다.")
    private final LocalDateTime endDate;
}
