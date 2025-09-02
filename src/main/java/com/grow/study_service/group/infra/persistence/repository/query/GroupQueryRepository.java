package com.grow.study_service.group.infra.persistence.repository.query;

import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.presentation.dto.GroupSimpleResponse;

import java.util.List;

public interface GroupQueryRepository {

    List<GroupSimpleResponse> findJoinedGroupsByMemberAndCategory(Long memberId, Category category);
}
