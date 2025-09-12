package com.grow.study_service.kanbanboard.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.kanbanboard.domain.enums.KanbanStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KanbanBoard {
	private final Long kanbanId;
	private final Long GroupMemberId;
	private String content;
	private KanbanStatus status;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	public static KanbanBoard create(Long groupMemberId,
									 String content,
									 LocalDateTime startDate,
									 LocalDateTime endDate
	) {
		return new KanbanBoard(
				null,
				groupMemberId,
				content,
				KanbanStatus.READY,
				startDate,
				endDate
		);
	}

	public void updateContent(String newContent) {
		if (newContent == null || newContent.isBlank()) {
			throw new IllegalArgumentException("할 일 내용은 비어 있을 수 없습니다.");
		}
		this.content = newContent;
	}

	public void markInProgress(LocalDateTime now) {
		this.status = KanbanStatus.IN_PROGRESS;
		this.startDate   = now;
	}

	public void markDone(LocalDateTime now) {
		this.status = KanbanStatus.DONE;
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
		if (this.status == KanbanStatus.DONE) {
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
