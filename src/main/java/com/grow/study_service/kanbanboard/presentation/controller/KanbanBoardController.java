package com.grow.study_service.kanbanboard.presentation.controller;


import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.kanbanboard.application.KanbanBoardService;
import com.grow.study_service.kanbanboard.presentation.dto.TodoCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
                response
        );
    }
}
