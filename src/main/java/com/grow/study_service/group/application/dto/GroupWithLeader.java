package com.grow.study_service.group.application.dto;

import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupWithLeader {

    private Group group;
    private GroupMember leader;
}
