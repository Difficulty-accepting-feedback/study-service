package com.grow.study_service.group.infra.persistence.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.group.domain.enums.Category;
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

	// 카테고리별 전체 그룹을 조회
	@Override
	public List<Group> findAllByCategory(Category category) {
        return groupJpaRepository.findAllByCategory(category)
				.stream()
				.map(GroupMapper::toDomain)
				.toList();
	}

	/**
	 * 주어진 그룹 ID로 그룹 이름을 조회합니다.
	 * 그룹이 존재하지 않으면 null을 반환합니다.
	 *
	 * @param groupId 조회할 그룹 ID
	 * @return 그룹 이름 (String), 없으면 null
	 */
	@Override
	public String findGroupNameById(Long groupId) {
		return groupJpaRepository.findGroupNameById(groupId);
	}

	/**
	 * 주어진 그룹 이름이 이미 존재하는지 확인합니다.
	 * @param groupName - 검사할 그룹 이름 (필수, null 불가)
	 * @return 그룹 이름이 이미 존재하는지 여부 (true/false) - 존재하면 true, 없으면 false를 반환합니다.
	 */
	@Override
	public boolean existsByGroupName(String groupName) {
		return groupJpaRepository.existsByName((groupName));
	}
}
