package com.grow.study_service.notice.infra.repository;

import com.grow.study_service.notice.domain.model.Notice;
import com.grow.study_service.notice.domain.repository.NoticeRepository;
import com.grow.study_service.notice.infra.entity.NoticeJpaEntity;
import com.grow.study_service.notice.infra.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * NoticeRepositoryImpl 클래스
 * <p>
 * NoticeRepository 인터페이스의 구현체로,
 * JPA 기반 데이터 접근 로직을 캡슐화한다.
 * </p>
 * <p>
 * Notice 도메인 모델을 JPA 엔티티로 변환한 뒤,
 * Spring Data JPA 리포지토리를 통해 영속화 작업을 수행한다.
 * </p>
 */
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeMapper mapper;
    private final NoticeJpaRepository noticeJpaRepository;

    /**
     * Notice 객체를 저장한다.
     * <p>
     * - 신규 생성 시: ID 없이 저장
     * - 수정 시: ID 포함 엔티티를 저장하여 업데이트 처리
     * </p>
     *
     * @param notice 저장할 Notice 도메인 객체
     */
    @Override
    public void save(Notice notice) {
        NoticeJpaEntity entity = mapper.toEntity(notice);
        noticeJpaRepository.save(entity);
    }
}
