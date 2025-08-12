package com.grow.study_service.notice.infra.repository;

import com.grow.study_service.notice.infra.entity.NoticeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<NoticeJpaEntity, Long> {
}
