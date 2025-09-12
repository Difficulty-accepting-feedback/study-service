package com.grow.study_service.kanbanboard.presentation.controller;


import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.kanbanboard.application.KanbanBoardService;
import com.grow.study_service.kanbanboard.presentation.dto.TodoCreateRequest;
import com.grow.study_service.kanbanboard.presentation.dto.response.KanbanBoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/study/kanbanBoard")
public class KanbanBoardController {

    private final KanbanBoardService kanbanBoardService;

    // 칸반보드에 일정 등록
    @PostMapping("/todos/{groupId}")
    public RsData<Long> createTodo(@RequestHeader("X-Authorization-Id") Long memberId,
                                   @PathVariable("groupId") Long groupId,
                                   @RequestBody TodoCreateRequest request) {

        // 클라이언트에서 ID 를 받아 라우팅 하기 위함
        Long response = kanbanBoardService.createTodo(memberId, groupId, request);

        return new RsData<>(
                "201",
                "투두 등록 완료",
                response // 칸반보드 ID 단건
        );
    }

    // 칸반보드 조회 (한 달 주기로 조회)
    @GetMapping("/todos/{groupId}")
    public RsData<List<KanbanBoardResponse>> getTodos(@RequestHeader("X-Authorization-Id") Long memberId,
                                                      @PathVariable("groupId") Long groupId) {

        List<KanbanBoardResponse> responses = kanbanBoardService.getTodos(memberId, groupId);

        return new RsData<>(
                "200",
                "투두 조회 완료",
                responses // 칸반보드 리스트
        );
    }
}
