package com.grow.study_service.group.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.group.application.create.GroupCreateService;
import com.grow.study_service.group.presentation.dto.create.GroupCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupCreateController {

    private final GroupCreateService groupCreateService;

    // 그룹 생성 API
    @PostMapping("/create")
    public ResponseEntity<RsData<Long>> createGroup(@RequestHeader("X-Authorization-Id") Long memberId,
                                                    @RequestBody GroupCreateRequest request) {

        Long groupId = groupCreateService.createGroup(request, memberId);
        RsData<Long> response = new RsData<>(
                "201",
                "그룹 생성 완료",
                groupId
        );

        return ResponseEntity.ok(response);
    }
}