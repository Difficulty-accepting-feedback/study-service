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
		if (newContent == null || newContent.isBlank()) {
			throw new IllegalArgumentException("할 일 내용은 비어 있을 수 없습니다.");
		}
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
		if (newStart == null || newEnd == null) {
			throw new IllegalArgumentException("시작일과 종료일은 반드시 지정해야 합니다.");
		}
		if (newEnd.isBefore(newStart)) {
			throw new IllegalArgumentException("종료일은 시작일 이후여야 합니다.");
		}
		if (newStart.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("시작일은 현재 이후여야 합니다.");
		}
		if (this.isCompleted == KanbanStatus.DONE) {
			throw new IllegalStateException("완료된 할 일은 일정을 변경할 수 없습니다.");
		}

		this.startDate = newStart;
		this.endDate   = newEnd;
	}

	public static KanbanBoard of(Long toDoId, Long groupMemberId, String toDoContent, KanbanStatus isCompleted,
			LocalDateTime startDate, LocalDateTime endDate) {
		return new KanbanBoard(toDoId, groupMemberId, toDoContent, isCompleted, startDate, endDate);
	}
}
