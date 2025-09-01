package com.grow.study_service.group.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.group.application.GroupFacadeService;
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

    private final GroupFacadeService groupFacadeService;

    // 전체 그룹 조회 (카테고리 별 조회 가능)
    @GetMapping()
    public RsData<List<GroupResponse>> getGroups(@RequestParam("category") Category category) {

        List<GroupResponse> responses = groupFacadeService.getAllGroupsByCategory(category);

        return new RsData<>(
                "200",
                "그룹 조회 완료",
                responses
        );
    }

    // 특정 그룹 조회 (ID)
    @GetMapping("/{groupId}")
    public RsData<GroupDetailResponse> getSingleGroup(@PathVariable("groupId") Long groupId) {

        GroupDetailResponse response = groupFacadeService.getGroupByCategory(groupId);

        return new RsData<>(
                "200",
                "특정 그룹 조회 완료",
                response
        );
    }
}
