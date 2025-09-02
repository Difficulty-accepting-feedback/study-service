package com.grow.study_service.group.application.api;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.grow.study_service.group.application.api.MemberApiServiceImpl.*;

public interface MemberApiService {
    /**
     * 단일 멤버 이름 조회
     */
    String getMemberName(Long memberId);

    /**
     * 외부 API 호출로 멤버 이름 조회 (리스트 처리)
     */
    List<String> fetchMemberNames(List<Long> memberIds);

    /**
     * 가입 요청 정보 확인
     */
    Mono<List<MemberInfo>> getNicknameAndScore(List<Long> memberIds);
}
