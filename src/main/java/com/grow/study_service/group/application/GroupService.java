package com.grow.study_service.group.application;

import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.presentation.dto.GroupDetailResponse;
import com.grow.study_service.group.presentation.dto.GroupResponse;

import java.util.List;

public interface GroupService {
    List<GroupResponse> getAllGroupsByCategory(Category category);
    GroupDetailResponse getGroup(Long groupId);
}
