package com.grow.study_service.group.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.group.application.GroupService;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.presentation.dto.GroupDetailResponse;
import com.grow.study_service.group.presentation.dto.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupFindController {

    private final GroupService groupService;

    // 전체 그룹 조회 (카테고리 별 조회 가능)
    @GetMapping("/{category}")
    public RsData<List<GroupResponse>> getGroups(@PathVariable("category") Category category) {

        List<GroupResponse> responses = groupService.getAllGroupsByCategory(category);

        return new RsData<>(
                "200",
                "그룹 조회 완료",
                responses
        );
    }

    // 특정 그룹 조회 (ID)
    @GetMapping("/{category}/{groupId}")
    public RsData<GroupDetailResponse> getSingleGroup(@PathVariable("category") Category category,
                                                      @PathVariable("groupId") Long groupId) {
        GroupDetailResponse response = groupService.getGroup(groupId);

        return new RsData<>(
                "200",
                "특정 그룹 조회 완료",
                response
        );
    }
}
