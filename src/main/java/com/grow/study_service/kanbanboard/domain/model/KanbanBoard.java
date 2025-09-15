package com.grow.study_service.kanbanboard.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.kanbanboard.domain.enums.KanbanStatus;

import com.grow.study_service.kanbanboard.presentation.dto.TodoCreateRequest;
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


	/**
	 * TO-DO의 내용, 상태, 날짜를 업데이트합니다.
	 * 이미 완료된 상태(DONE)인 경우 변경을 금지하며, 변경된 필드만 업데이트합니다.
	 *
	 * @param request TO-DO 업데이트 요청 객체
	 * @throws DomainException 이미 완료된 TO-DO를 변경하려 할 경우 CANNOT_CHANGE_STATUS_OF_COMPLETED_TODO 오류 발생
	 */
	public void updateTodo(TodoCreateRequest request) {
		// 이미 완료된 할 일은 일정을 변경할 수 없음
		// (이게 합리적인가 모르겠긴 함 근데 일단 제약이 좀 있어야 할 거 같고...)
		if (this.status == KanbanStatus.DONE) {
			throw new DomainException(ErrorCode.CANNOT_CHANGE_STATUS_OF_COMPLETED_TODO);
		}

		// 상태가 변경되었는지 확인
		if (!(this.status).equals(request.getStatus())) {
			this.status = request.getStatus();
		}

		// 내용이 변경되었는지 확인
		if (!this.content.equals(request.getContent())) {
			this.content = request.getContent();
		}

		// 날짜가 변경되었는지 확인
		if (!this.startDate.equals(request.getStartDate())) {
			this.startDate = request.getStartDate();
		}

		if (!this.endDate.equals(request.getEndDate())) {
			this.endDate = request.getEndDate();
		}
	}

	/**
	 * 준비 중인 칸반보드의 Status 를 진행 중으로 변경합니다.
	 */
	public void updateStatus() {
		if (this.status == KanbanStatus.READY) {
			this.status = KanbanStatus.IN_PROGRESS; // 진행 중으로 상태 변경
		}
	}
}
