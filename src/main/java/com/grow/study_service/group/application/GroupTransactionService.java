package com.grow.study_service.group.application;

import com.grow.study_service.group.application.dto.GroupDetailPrep;
import com.grow.study_service.group.application.dto.GroupWithLeader;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.presentation.dto.GroupDetailResponse;
import com.grow.study_service.group.presentation.dto.GroupResponse;

import java.util.List;

public interface GroupTransactionService {
    List<GroupWithLeader> prepareGroupsByCategory(Category category);

    List<GroupResponse> buildGroupResponses(List<GroupWithLeader> groupsWithLeaders, List<String> memberNames, Category category);

    GroupDetailPrep prepareGroupDetail(Long groupId);

    GroupDetailResponse buildGroupDetailResponse(GroupDetailPrep prep, String leaderName, Long groupId);
}
