package com.grow.study_service.group.domain.repository;

import java.util.Optional;

import com.grow.study_service.group.domain.model.Group;

public interface GroupRepository {
	Group save(Group group);
	Optional<Group> findById(Long groupId);
	void delete(Group group);
}
