package com.grow.study_service.kanbanboard.infra.persistence.mapper;

import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;
import com.grow.study_service.kanbanboard.infra.persistence.entity.KanbanBoardJpaEntity;

public class KanbanBoardMapper {

	public static KanbanBoard toDomain(KanbanBoardJpaEntity e) {
		return KanbanBoard.of(
			e.getToDoId(),
			e.getGroupMemberId(),
			e.getToDoContent(),
			e.getIsCompleted(),
			e.getStartDate(),
			e.getEndDate()
		);
	}

	public static KanbanBoardJpaEntity toEntity(KanbanBoard d) {
		return KanbanBoardJpaEntity.builder()
			.toDoId(d.getToDoId())
			.groupMemberId(d.getGroupMemberId())
			.toDoContent(d.getToDoContent())
			.isCompleted(d.getIsCompleted())
			.startDate(d.getStartDate())
			.endDate(d.getEndDate())
			.build();
	}
}