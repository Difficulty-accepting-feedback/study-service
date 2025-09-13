package com.grow.study_service.kanbanboard.application;

import com.grow.study_service.kanbanboard.presentation.dto.TodoCreateRequest;
import com.grow.study_service.kanbanboard.presentation.dto.response.KanbanBoardResponse;

import java.util.List;

public interface KanbanBoardService {
    Long createTodo(Long memberId, Long groupId, TodoCreateRequest request);

    List<KanbanBoardResponse> getTodos(Long memberId, Long groupId);

    Long updateTodo(Long memberId, TodoCreateRequest request, Long kanbanId);
}