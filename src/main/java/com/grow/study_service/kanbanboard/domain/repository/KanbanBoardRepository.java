package com.grow.study_service.kanbanboard.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;

public interface KanbanBoardRepository {
	KanbanBoard save(KanbanBoard kanbanBoard);
	Optional<KanbanBoard> findById(Long toDoId);
	void delete(KanbanBoard kanbanBoard);
	List<KanbanBoard> findByGroupMemberIdAndDateBetween(Long groupMemberId, LocalDateTime startDate, LocalDateTime endDate);
}
