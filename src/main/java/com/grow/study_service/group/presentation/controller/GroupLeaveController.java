package com.grow.study_service.group.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupLeaveController {

    @DeleteMapping("/leave-group/{groupId}")
    public RsData<String> leaveGroup(@RequestHeader("X-Authorization-Id") Long memberId,
                                     @PathVariable("groupId") Long groupId) {

        // TODO 그룹 탈퇴 로직 추가

        return new RsData<>(
                "200",
                "그룹 탈퇴 완료"
        );
    }
}