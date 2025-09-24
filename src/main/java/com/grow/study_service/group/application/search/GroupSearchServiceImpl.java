package com.grow.study_service.group.application.search;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.grow.study_service.group.domain.document.GroupDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupSearchServiceImpl implements GroupSearchService {

    // 엘라스틱 서치로 쿼리를 전송하는 용도
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 검색어를 입력받아 그룹 검색을 수행합니다. (엘라스틱 서치 이용)
     *
     * @param query - 검색어 (필수)
     * @return 검색어에 일치하는 그룹 이름 리스트 (엘라스틱 서치에서 반환하는 값)
     */
    @Override
    public List<String> getGroupSuggestions(String query) {
        // 쿼리에 담긴 글자와 일치하는 자동 추천 검색어를 가져 옴
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(query)
                .type(TextQueryType.BoolPrefix)
                .fields(
                        "name.auto_complete",
                        "name.auto_complete._2gram",
                        "name.auto_complete._3gram"
                )
        )._toQuery();

        // 내가 작성한 쿼리를 넣을 수 있는 껍데기 (여기에 넣어야 엘라스틱 서치에 전송이 가능)
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(multiMatchQuery)
                .withPageable(PageRequest.of(0, 5)) // 다섯 개까지만 추천 검색어로 등장할 수 있도록
                .build();

        // 쿼리를 보내서 검색어 리스트를 가져옴
        SearchHits<GroupDocument> searchHits = elasticsearchOperations.search(nativeQuery, GroupDocument.class);// 쿼리와 반환 타입 넣기

        // 검색어 리스트에서 가져온 값을 리스트로 변환하여 반환
        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    GroupDocument groupDocument = hit.getContent();
                    return groupDocument.getName();
                })
                .toList();
    }
}
