package com.grow.study_service.group.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinRequest {

    private final Long leaderId;
    private final Long groupId;
}
