package com.grow.study_service.group.kanbanboard.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.group.kanbanboard.domain.enums.KanbanStatus;

import lombok.Getter;

@Getter
public class KanbanBoard {
	private final Long toDoId;
	private final Long GroupMemberId;
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

	public void updateContent(String newContent) {
		this.toDoContent = newContent;
	}

	public void markInProgress(LocalDateTime now) {
		this.isCompleted = KanbanStatus.IN_PROGRESS;
		this.startDate   = now;
	}

	public void markDone(LocalDateTime now) {
		this.isCompleted = KanbanStatus.DONE;
		this.endDate     = now;
	}

	public void reschedule(LocalDateTime newStart, LocalDateTime newEnd) {
		this.startDate = newStart;
		this.endDate   = newEnd;
	}

	public static KanbanBoard of(Long toDoId, Long groupMemberId, String toDoContent, KanbanStatus isCompleted,
			LocalDateTime startDate, LocalDateTime endDate) {
		return new KanbanBoard(toDoId, groupMemberId, toDoContent, isCompleted, startDate, endDate);
	}
}
