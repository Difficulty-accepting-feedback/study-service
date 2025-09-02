package com.grow.study_service.group.presentation.dto.join;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinInfoResponse {

    private final Long groupId;
    private final String groupName;
}