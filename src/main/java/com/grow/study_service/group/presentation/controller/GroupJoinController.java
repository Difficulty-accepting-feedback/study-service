package com.grow.study_service.group.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.group.application.join.GroupJoinService;
import com.grow.study_service.group.presentation.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupJoinController {

    private final GroupJoinService groupJoinService;

    // 그룹에 가입 요청 전송 API
    @PostMapping("/join-request")
    public RsData<Void> sendJoinRequestToGroup(@RequestHeader("X-Authorization-Id") Long memberId,
                                  @RequestBody JoinRequest request) {

        groupJoinService.sendJoinRequest(request, memberId);

        return new RsData<>(
                "201",
                "그룹에 가입 요청 전송 완료"
        );
    }

    // (멘토링) 그룹에 가입 전송 API (결제 후 자동 가입 진행)
    @PostMapping("/join/{groupId}")
    public RsData<Void> joinMentoring(@RequestHeader("X-Authorization-Id") Long memberId,
                                  @PathVariable("groupId") Long groupId) {

        groupJoinService.joinGroup(memberId, groupId); // TODO 결제 서비스로 요청을 전송해야 함

        return new RsData<>(
                "201",
                "그룹에 가입 요청 전송 완료"
        );
    }
}
