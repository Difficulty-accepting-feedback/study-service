package com.grow.study_service.group.application.api;

import java.util.List;

public interface MemberApiService {
    /**
     * 단일 멤버 이름 조회
     */
    String getMemberName(Long memberId);

    /**
     * 외부 API 호출로 멤버 이름 조회 (리스트 처리)
     */
    List<String> fetchMemberNames(List<Long> memberIds);
}
