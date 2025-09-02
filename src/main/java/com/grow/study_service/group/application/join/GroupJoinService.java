package com.grow.study_service.group.application.join;

import com.grow.study_service.group.presentation.dto.JoinRequest;

public interface GroupJoinService {
    void joinGroup(Long memberId, Long groupId);
    void sendJoinRequest(JoinRequest request, Long memberId);
}
