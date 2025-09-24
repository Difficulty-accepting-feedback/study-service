package com.grow.study_service.group.application.search;

import java.util.List;

public interface GroupSearchService {
    List<String> getGroupSuggestions(String query);
}