package com.grow.study_service.group.application.dto;

import com.grow.study_service.group.domain.model.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupDetailPrep {

    private Group group;
    private int memberCount;
    private Long leaderId;
}
