package com.grow.study_service.notice.infra.mapper;

import com.grow.study_service.notice.domain.model.Notice;
import com.grow.study_service.notice.infra.entity.NoticeJpaEntity;
import com.grow.study_service.notice.infra.entity.NoticeJpaEntity.NoticeJpaEntityBuilder;
import org.springframework.stereotype.Component;

/**
 * NoticeMapper 클래스
 * <p>
 * Notice 도메인 모델과 JPA 엔티티 간의 변환 로직을 담당한다.
 * </p>
 * <ul>
 *     <li>조회 시: 엔티티 → 도메인 변환</li>
 *     <li>저장/수정 시: 도메인 → 엔티티 변환</li>
 * </ul>
 */
@Component
public class NoticeMapper {

    /**
     * JPA 엔티티를 도메인 객체로 변환한다.
     *
     * @param entity NoticeJpaEntity 객체 (DB 조회 결과)
     * @return 변환된 Notice 도메인 객체
     */
    public Notice toDomain(NoticeJpaEntity entity) {
        return Notice.of(
                entity.getId(),
                entity.getGroupId(),
                entity.getContent(),
                entity.isPinned()
        );
    }

    /**
     * 도메인 객체를 JPA 엔티티로 변환한다.
     * <p>
     * 새로운 Notice 저장 또는 기존 Notice 업데이트 시 사용된다.
     * </p>
     *
     * @param domain Notice 도메인 객체
     * @return 변환된 NoticeJpaEntity 객체
     */
    public NoticeJpaEntity toEntity(Notice domain) {
        NoticeJpaEntityBuilder builder = NoticeJpaEntity.builder()
                .groupId(domain.getGroupId())
                .content(domain.getContent())
                .isPinned(domain.isPinned());

        // 기존 Notice인 경우 ID를 설정하여 업데이트 가능하게 함
        if (domain.getNoticeId() != null) {
            builder.id(domain.getNoticeId());
        }

        return builder.build();
    }
}