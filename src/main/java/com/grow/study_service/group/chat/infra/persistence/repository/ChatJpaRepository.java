package com.grow.study_service.group.chat.infra.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.grow.study_service.group.chat.infra.persistence.entity.ChatJpaEntity;

public interface ChatJpaRepository
	extends JpaRepository<ChatJpaEntity, Long> {
	List<ChatJpaEntity> findByGroupId(Long groupId);
}