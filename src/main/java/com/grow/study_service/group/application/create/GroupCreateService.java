package com.grow.study_service.group.application.create;

import com.grow.study_service.group.presentation.dto.create.GroupCreateRequest;

public interface GroupCreateService {
    Long createGroup(GroupCreateRequest request, Long memberId);
}