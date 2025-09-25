package com.grow.study_service.group.application.search;

import com.grow.study_service.group.domain.document.GroupDocument;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.enums.SkillTag;

import java.util.List;

public interface GroupSearchService {
    List<String> getGroupSuggestions(String query);
    List<GroupDocument> searchGroup(String query, Category category, SkillTag skillTag, String sortBy, String sortOrder, int page, int size);
}