package com.grow.study_service.group.infra.persistence.repository;

import com.grow.study_service.group.domain.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import com.grow.study_service.group.infra.persistence.entity.GroupJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupJpaRepository
	extends JpaRepository<GroupJpaEntity, Long> {
    List<GroupJpaEntity> findAllByCategory(Category category);

    @Query("select g.name from GroupJpaEntity g where g.id = :groupId")
    String findGroupNameById(@Param("groupId") Long groupId);
}
