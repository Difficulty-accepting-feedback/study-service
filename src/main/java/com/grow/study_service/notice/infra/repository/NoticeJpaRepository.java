package com.grow.study_service.notice.infra.repository;

import com.grow.study_service.notice.infra.entity.NoticeJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeJpaRepository extends JpaRepository<NoticeJpaEntity, Long> {
    List<NoticeJpaEntity> findByGroupId(Long groupId);

    /**
     * 주어진 그룹 ID에 속하고 고정된(isPinned=true) 공지사항 목록을 조회합니다.
     *
     * @param groupId 조회할 그룹 ID
     * @return 해당 조건을 만족하는 NoticeJpaEntity 목록 (결과가 없을 경우 빈 리스트 반환)
     */
    @Query("select n from NoticeJpaEntity n " +
            "where n.groupId = :groupId and n.isPinned = true")
    List<NoticeJpaEntity> findByGroupIdAndPinnedIsTrue(@Param("groupId") Long groupId);

}
