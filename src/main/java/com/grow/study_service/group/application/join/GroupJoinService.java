package com.grow.study_service.group.application.join;

import com.grow.study_service.group.presentation.dto.join.JoinConfirmRequest;
import com.grow.study_service.group.presentation.dto.join.JoinInfoResponse;
import com.grow.study_service.group.presentation.dto.join.JoinRequest;

import java.util.List;

public interface GroupJoinService {
    void joinGroup(Long memberId, Long groupId);
    void sendJoinRequest(JoinRequest request, Long memberId);
    List<JoinInfoResponse> findGroupIdsByLeaderId(Long memberId);
    List<Long> prepareFindJoinRequest(Long groupId);
    void acceptJoinRequest(Long memberId, JoinConfirmRequest request);
    void rejectJoinRequest(Long memberId, JoinConfirmRequest request);
}
