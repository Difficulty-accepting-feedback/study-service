package com.grow.study_service.kanbanboard.infra.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.kanbanboard.infra.persistence.entity.KanbanBoardJpaEntity;
import org.springframework.data.jpa.repository.Query;

public interface KanbanBoardJpaRepository
        extends JpaRepository<KanbanBoardJpaEntity, Long> {
    List<KanbanBoardJpaEntity> findByGroupMemberId(Long groupMemberId);

    /**
     * 그룹 멤버 ID를 기준으로 지정된 시작 날짜 범위 내의 KanbanBoardJpaEntity 목록을 조회합니다.
     * startDate 필드가 startDate와 endDate 사이에 있는 엔티티를 startDate 오름차순으로 정렬하여 반환합니다.
     *
     * @param groupMemberId 그룹 멤버 ID
     * @param startDate     범위 시작 날짜/시간 (포함)
     * @param endDate       범위 종료 날짜/시간 (포함)
     * @return 해당 범위 내 KanbanBoardJpaEntity 목록 (startDate 오름차순 정렬)
     */
    @Query("SELECT k FROM KanbanBoardJpaEntity k " +
            "WHERE k.groupMemberId = :groupMemberId " +
            "AND k.startDate BETWEEN :startDate AND :endDate " +
            "order by k.startDate asc")
    List<KanbanBoardJpaEntity> findByGroupMemberIdAndDateBetween(@Param("groupMemberId") Long groupMemberId,
                                                                 @Param("startDate") LocalDateTime startDate,
                                                                 @Param("endDate") LocalDateTime endDate);
}