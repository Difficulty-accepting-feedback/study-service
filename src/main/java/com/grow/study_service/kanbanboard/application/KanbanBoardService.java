package com.grow.study_service.kanbanboard.application;

import com.grow.study_service.kanbanboard.presentation.dto.TodoCreateRequest;

public interface KanbanBoardService {
    Long createTodo(Long memberId, Long groupId, TodoCreateRequest request);
}