package com.grow.study_service.group.kanbanboard.infra.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grow.study_service.group.kanbanboard.domain.model.KanbanBoard;
import com.grow.study_service.group.kanbanboard.domain.repository.KanbanBoardRepository;
import com.grow.study_service.group.kanbanboard.infra.persistence.entity.KanbanBoardJpaEntity;
import com.grow.study_service.group.kanbanboard.infra.persistence.mapper.KanbanBoardMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class KanbanBoardRepositoryImpl implements KanbanBoardRepository {

	private final KanbanBoardJpaRepository kanbanBoardJpaRepository;

	@Override
	public KanbanBoard save(KanbanBoard board) {
		KanbanBoardJpaEntity saved = kanbanBoardJpaRepository.save(KanbanBoardMapper.toEntity(board));
		return KanbanBoardMapper.toDomain(saved);
	}

	@Override
	public Optional<KanbanBoard> findById(Long toDoId) {
		return kanbanBoardJpaRepository.findById(toDoId)
			.map(KanbanBoardMapper::toDomain);
	}

	@Override
	public List<KanbanBoard> findByGroupMemberId(Long groupMemberId) {
		return kanbanBoardJpaRepository.findByGroupMemberId(groupMemberId).stream()
			.map(KanbanBoardMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void delete(KanbanBoard board) {
		kanbanBoardJpaRepository.delete(KanbanBoardMapper.toEntity(board));
	}
}