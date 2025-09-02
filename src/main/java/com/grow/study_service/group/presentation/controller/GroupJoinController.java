package com.grow.study_service.group.presentation.controller;

import com.grow.study_service.common.rsdata.RsData;
import com.grow.study_service.group.application.GroupFacadeService;
import com.grow.study_service.group.application.api.MemberApiServiceImpl.MemberInfo;
import com.grow.study_service.group.application.join.GroupJoinService;
import com.grow.study_service.group.presentation.dto.join.JoinConfirmRequest;
import com.grow.study_service.group.presentation.dto.join.JoinInfoResponse;
import com.grow.study_service.group.presentation.dto.join.JoinRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupJoinController {

    private final GroupJoinService groupJoinService;
    private final GroupFacadeService groupFacadeService;

    // 그룹에 가입 요청 전송 API
    @PostMapping("/send/join-request")
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

    // 그룹 확인 API (그룹장 전용)
    @GetMapping("/check/join-request")
    public RsData<List<JoinInfoResponse>> checkJoinRequestByGroupId(@RequestHeader("X-Authorization-Id") Long memberId) {

        List<JoinInfoResponse> infos = groupJoinService.findGroupIdsByLeaderId(memberId);

        log.info("[GROUP][JOIN][END] memberId={} - 그룹 리스트 조회 완료", memberId);
        return new RsData<>(
                "200",
                "그룹별 가입 요청을 위한 그룹 ID 목록 반환 완료",
                infos);
    }

    // 그룹별 가입 요청 확인 API (ID 기반, 그룹장 전용)
    @GetMapping("/check/join-request/{groupId}")
    public RsData<List<MemberInfo>> checkJoinRequest(@PathVariable("groupId") Long groupId) {

        List<MemberInfo> responses = groupFacadeService.getJoinMemberInfo(groupId);

        return new RsData<>("200",
                "가입 요청 확인 완료",
                responses);
    }

    // 가입 요청 수락 API
    @PostMapping("/accept-request")
    public RsData<Void> acceptJoinRequest(@RequestHeader("X-Authorization-Id") Long memberId,
                                          @RequestBody JoinConfirmRequest request) {

        groupJoinService.acceptJoinRequest(memberId, request);

        return new RsData<>(
                "200",
                "가입 요청 수락 완료"
        );
    }

    // 가입 요청 거절 API
    @PostMapping("/reject-request")
    public RsData<Void> rejectJoinRequest(@RequestHeader("X-Authorization-Id") Long memberId,
                                          @RequestBody JoinConfirmRequest request) {

        groupJoinService.rejectJoinRequest(memberId, request);

        return new RsData<>(
                "200",
                "가입 요청 거절 완료"
        );
    }
}
