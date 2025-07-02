package com.grow.study_service.group.infra.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.group.infra.persistence.mapper.GroupMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {
	private final GroupJpaRepository groupJpaRepository;

	@Override
	public Group save(Group group) {
		return GroupMapper.toDomain(groupJpaRepository.save(GroupMapper.toEntity(group)));
	}

	@Override
	public Optional<Group> findById(Long groupId) {
		return groupJpaRepository.findById(groupId)
				.map(GroupMapper::toDomain);
	}

	@Override
	public void delete(Group group) {
		groupJpaRepository.delete(GroupMapper.toEntity(group));
	}
}
