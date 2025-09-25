package com.grow.study_service.group.application.search;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.domain.document.GroupDocument;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.enums.SkillTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    /**
     * 검색어와 카테고리, 스킬 태그를 입력받아 그룹 검색을 수행합니다. (엘라스틱 서치 이용)
     *
     * @param query    - 검색어 (필수)
     * @param category - 카테고리 (선택)
     * @param skillTag - 스킬 태그 (선택)
     * @param sortBy   - 정렬 기준 (선택)
     * @param sortOrder- 정렬 방향 (선택)
     * @param page     - 페이지 번호 (필수 값 있음)
     * @param size     - 페이지 크기 (필수 값 있음)
    */
    @Override
    public List<GroupDocument> searchGroup(String query, Category category,
                                           SkillTag skillTag, String sortBy,
                                           String sortOrder, int page, int size) {

        log.info("[GROUP][SEARCH][START] 검색어={}, 카테고리={}, 스킬 태그={}, 정렬 기준={}, 정렬 방향={}, 페이지={}, 크기={}",
                query, category, skillTag, sortBy, sortOrder, page, size);

        String sortField = mapSortByToField(sortBy);  // sortBy를 실제 필드명으로 매핑
        Direction direction = sortOrder.equals("desc") ? Direction.DESC : Direction.ASC; // 정렬 방향 지정

        // 각 단일 쿼리를 모두 따로 작성 -> 이후 bool 쿼리 안에 전부 넣기
        // multi_match 쿼리
        Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(query)
                .fields("name^3", "description^1", "category^2", "skillTag^2" )
                .fuzziness("AUTO")
        )._toQuery();

        // term filter 쿼리 : 스킬 태그가 정확히 일치하는 것만 필터링
        List<Query> filters = new ArrayList<>();

        if (skillTag != null) {
            Query skillTagQuery = TermQuery.of(t -> t
                    .field("skillTag.raw")
                    .value(skillTag.getDescription())
            )._toQuery();
            filters.add(skillTagQuery);
        }

        if (category != null) {
            Query categoryQuery = TermQuery.of(t -> t
                    .field("category.raw")
                    .value(category.getDescription())
            )._toQuery();
            filters.add(categoryQuery);
        }

        // should 쿼리: 해당 조건을 만족하면 가산점, 만족하지 않아도 괜찮음
        Query viewCountShould = NumberRangeQuery.of(r -> r
                .field("viewCount")
                .gte(300.0)
        )._toRangeQuery()._toQuery();

        // 이 쿼리를 모두 담을 bool 쿼리 생성
        Query boolQuery = BoolQuery.of(b -> b
                .must(multiMatchQuery)
                .filter(filters)
                .should(viewCountShould))
                ._toQuery();

        // 페이지에 보여줄 때 하이라이팅 하기 위한 설정 추가
        HighlightParameters highlightParams = HighlightParameters.builder()
                .withPreTags("<b>")
                .withPostTags("</b>")
                .build();

        Highlight highlight = new Highlight(highlightParams, List.of(new HighlightField("name")));

        HighlightQuery highlightQuery = new HighlightQuery(highlight, GroupDocument.class);

        // 네이티브 쿼리 껍데기 작성
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                // .withPageable(PageRequest.of(page - 1, size, Sort.by(direction, sortField))) // 페이지네이션 + 정렬
                .withSort(Sort.by(direction, sortField))
                .withHighlightQuery(highlightQuery)
                .build();

        log.info("Generated Query: {}", Objects.requireNonNull(nativeQuery.getQuery()));

        SearchHits<GroupDocument> searchHits = elasticsearchOperations.search(nativeQuery, GroupDocument.class);

        log.info("[GROUP][SEARCH][END] 검색 완료, 검색 결과 개수={}", searchHits.getTotalHits());

        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    GroupDocument groupDocument = hit.getContent();
                    if (!hit.getHighlightField("name").isEmpty()) {
                        String highlightedName = hit.getHighlightField("name").getFirst();
                        groupDocument.updateName(highlightedName);
                    }
                    return groupDocument;
                })
                .toList();
    }

    /**
     * sortBy를 실제 GroupDocument 필드명으로 매핑하는 헬퍼 메서드
     * @param sortBy - 정렬 기준 (ex: startAt, rating, viewCount 등)
     * @return 정렬 기준 필드명 (ex: startAt, rating, viewCount 등)
     */
    private String mapSortByToField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "startat" -> "startAt"; // 최신순
            // case "rating":  // 별점 높은 순 (rating 필드 추가 필요) - 혹은, 리더의 점수가 높은 기준으로 할 수도 있겠음 그룹이 얼마나 활발하게 운영되고 있는지 확인할 수 있는 지표가 있으면 좋을 듯
            // return "rating";
            case "viewcount" -> "viewCount"; // 조회수 높은 순
            default -> throw new ServiceException(ErrorCode.INVALID_SORT_BY_VALUE);
        };
    }
}
