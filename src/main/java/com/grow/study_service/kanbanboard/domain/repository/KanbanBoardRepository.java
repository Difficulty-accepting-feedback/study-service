package com.grow.study_service.kanbanboard.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;

public interface KanbanBoardRepository {
	KanbanBoard save(KanbanBoard kanbanBoard);
	Optional<KanbanBoard> findById(Long toDoId);
	List<KanbanBoard> findByGroupMemberId(Long groupMemberId);
	void delete(KanbanBoard kanbanBoard);
}
