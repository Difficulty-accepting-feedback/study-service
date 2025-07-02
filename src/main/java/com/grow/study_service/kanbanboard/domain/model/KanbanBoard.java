package com.grow.study_service.kanbanboard.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.kanbanboard.domain.enums.KanbanStatus;

import lombok.Getter;

@Getter
public class KanbanBoard {
	private Long toDoId;
	private Long GroupMemberId;
	private String toDoContent;
	private KanbanStatus isCompleted;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	private KanbanBoard (Long toDoId, Long groupMemberId, String toDoContent, KanbanStatus isCompleted,
			LocalDateTime startDate, LocalDateTime endDate) {
		this.toDoId = toDoId;
		this.GroupMemberId = groupMemberId;
		this.toDoContent = toDoContent;
		this.isCompleted = isCompleted;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static KanbanBoard create(Long groupMemberId, String toDoContent, LocalDateTime startDate, LocalDateTime endDate) {
		return new KanbanBoard(null, groupMemberId, toDoContent, KanbanStatus.READY, startDate, endDate);
	}

	public static KanbanBoard of(Long toDoId, Long groupMemberId, String toDoContent, KanbanStatus isCompleted,
			LocalDateTime startDate, LocalDateTime endDate) {
		return new KanbanBoard(toDoId, groupMemberId, toDoContent, isCompleted, startDate, endDate);
	}
}
