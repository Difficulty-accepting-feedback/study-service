package com.grow.study_service.board.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.board.domain.enums.BoardType;
import com.grow.study_service.group.domain.exception.DomainException;
import com.grow.study_service.group.domain.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시판 도메인 모델 클래스.
 * 이 클래스는 스터디 그룹 내 게시판의 정보를 관리하며, 게시판 생성, 조회, 이름 및 설명 업데이트 기능을 제공합니다.
 * 불변 필드(Immutable fields)를 사용하여 데이터의 안정성을 보장합니다.
 *
 * @author sun
 * @version 1.0
 * @since 2025-08-07
 */
@Getter
@AllArgsConstructor
public class Board {

    /**
     * 게시판의 고유 식별자(ID). 데이터베이스에서 자동 생성되며, null일 수 있습니다. (DB 에서 자동 생성된 ID 를 사용해야 함).
     */
    private final Long boardId;

    /**
     * 게시판이 속한 그룹의 고유 식별자(ID).
     */
    private final Long groupId;

    /**
     * 게시판이 생성된 날짜와 시간. 생성 시점에 설정되며, 변경되지 않습니다.
     */
    private final LocalDateTime createdAt;

    /**
     * 게시판의 유형. BoardType 열거형 값으로 관리됩니다.
     */
    private final BoardType boardType;

    /**
     * 게시판의 이름. 업데이트 가능하며, 비어 있지 않아야 합니다.
     */
    private String name;

    /**
     * 게시판의 상세 설명. 업데이트 가능하며, 비어 있지 않아야 합니다.
     */
    private String description;

    /**
     * 새로운 게시판을 생성하는 팩토리 메서드.
     * 게시판 ID는 null로 설정되며, 데이터베이스 삽입 시 자동 생성됩니다.
     *
     * @param groupId 게시판이 속한 그룹 ID. null 또는 0 이하일 수 없음.
     * @param boardType 게시판 유형. null일 수 없음.
     * @param name 게시판 이름. null 또는 공백일 수 없음.
     * @param description 게시판 설명. null 또는 공백일 수 없음.
     * @return 새로 생성된 Board 인스턴스.
     * @throws DomainException 매개변수가 유효하지 않으면 예외 발생
     */
    public static Board create(Long groupId,
                               BoardType boardType,
                               String name,
                               String description) {

        verifyParameters(groupId, boardType, name, description);
        return new Board(
                null,
                groupId,
                LocalDateTime.now(),
                boardType,
                name,
                description
        );
    }

    /**
     * 기존 게시판 정보를 바탕으로 Board 인스턴스를 생성하는 팩토리 메서드.
     * 주로 데이터베이스 조회 결과를 매핑할 때 사용됩니다.
     *
     * @param boardId 게시판 ID.
     * @param groupId 그룹 ID.
     * @param name 게시판 이름.
     * @param description 게시판 설명.
     * @param createdAt 생성 시각.
     * @param boardType 게시판 유형.
     * @return 조회된 정보를 담은 Board 인스턴스.
     * @throws DomainException 매개변수가 유효하지 않으면 예외 발생
     */
    public static Board of(Long boardId,
                           Long groupId,
                           String name,
                           String description,
                           LocalDateTime createdAt,
                           BoardType boardType) {
        verifyParameters(groupId, boardType, name, description);
        verifyIdAndCreatedAt(boardId, createdAt);
        return new Board(
                boardId,
                groupId,
                createdAt,
                boardType,
                name,
                description
        );
    }

    // ==== 업데이트 로직 ==== //

    /**
     * 게시판 이름을 업데이트하는 메서드.
     * 새로운 이름이 기존 이름과 다를 경우에만 업데이트하며, null 또는 공백 값은 허용되지 않습니다.
     *
     * @param newName 새로운 그룹 이름.
     * @throws DomainException 새 이름이 null 또는 공백일 경우 예외 발생.
     */
    public void updateBoardName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new DomainException(ErrorCode.BOARD_NAME_IS_EMPTY);
        }

        if (!this.name.equals(newName)) {
            this.name = newName;
        }
    }

    /**
     * 게시판 설명을 업데이트하는 메서드.
     * 새로운 설명이 기존 설명과 다를 경우에만 업데이트하며, null 값은 허용되지 않습니다.
     *
     * @param newDescription 새로운 게시판 설명.
     * @throws DomainException 새 설명이 null일 경우 예외 발생.
     */
    public void updateDescription(String newDescription) {
        if (newDescription == null) {
            throw new DomainException(ErrorCode.BOARD_DESCRIPTION_IS_EMPTY);
        }

        if (!this.description.equals(newDescription)) {
            this.description = newDescription;
        }
    }

    // ==== 검증 로직 ==== //
    private static void verifyParameters(Long groupId, BoardType boardType,
                                         String name, String description) {
        if (groupId == null || groupId <= 0L) {
            throw new DomainException(ErrorCode.GROUP_ID_IS_EMPTY);
        }

        if (boardType == null) {
            throw new DomainException(ErrorCode.BOARD_TYPE_IS_EMPTY);
        }

        if (name == null || name.isBlank()) {
            throw new DomainException(ErrorCode.BOARD_NAME_IS_EMPTY);
        }

        if (description == null || description.isBlank()) {
            throw new DomainException(ErrorCode.BOARD_DESCRIPTION_IS_EMPTY);
        }
    }

    private static void verifyIdAndCreatedAt(Long boardId, LocalDateTime createdAt) {
        if (boardId == null || boardId <= 0L) {
            throw new DomainException(ErrorCode.BOARD_ID_IS_EMPTY);
        }

        if (createdAt == null) {
            throw new DomainException(ErrorCode.BOARD_CREATED_AT_IS_EMPTY);
        }
    }
}
