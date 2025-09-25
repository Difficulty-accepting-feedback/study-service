package com.grow.study_service.group.presentation.controller.elastic;

import com.grow.study_service.group.application.search.GroupSearchService;
import com.grow.study_service.group.domain.document.GroupDocument;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.enums.SkillTag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/groups")
public class GroupSearchController {

    private final GroupSearchService searchService;

    // 자동 완성 API
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getGroupSuggestions(@RequestParam String query) {

        List<String> suggestions = searchService.getGroupSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    // 검색 API
    @GetMapping("/search")
    public ResponseEntity<List<GroupDocument>> getGroupSearch(@RequestParam String query, // 검색할 단어 (name + description + category + skillTag 에서 찾을 것임 (필수))
                                                              @RequestParam(required = false) Category category,
                                                              @RequestParam(required = false) SkillTag skillTag,
                                                              @RequestParam(required = false, defaultValue = "startAt") String sortBy,  // 정렬 기준 (기본: 생성 순) [조회수 순 or 생성 순]
                                                              @RequestParam(required = false, defaultValue = "desc") String sortOrder,  // 정렬 방향 (asc/desc)
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {

        List<GroupDocument> groupDocuments = searchService.searchGroup(query, category, skillTag, sortBy, sortOrder, page, size);
        return ResponseEntity.ok(groupDocuments);
    }
}
