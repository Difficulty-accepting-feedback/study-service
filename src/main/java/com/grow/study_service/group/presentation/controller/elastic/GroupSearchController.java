package com.grow.study_service.group.presentation.controller.elastic;

import com.grow.study_service.group.application.search.GroupSearchService;
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
}
