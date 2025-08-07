package com.grow.study_service.group.domain.model;

import com.grow.study_service.group.domain.exception.DomainException;
import com.grow.study_service.group.domain.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import com.grow.study_service.group.domain.enums.Category;

/**
 * 그룹 도메인 모델 클래스.
 * 이 클래스는 스터디 그룹의 정보를 관리하며, 그룹 생성, 조회, 이름 및 설명 업데이트 기능을 제공합니다.
 * 불변 필드(Immutable fields)를 사용하여 데이터의 안정성을 보장합니다.
 *
 * @author sun
 * @version 1.0
 * @since 2025-08-07
 */
@Getter
@AllArgsConstructor
public class Group {

    /**
     * 그룹의 고유 식별자(ID). 데이터베이스에서 자동 생성되며, null일 수 있습니다.
     */
    private final Long groupId;

    /**
     * 그룹의 카테고리. 예를 들어, 스터디 주제나 취미 등의 유형을 나타내는 열거형 값입니다.
     */
    private final Category category;

    /**
     * 그룹이 생성된 날짜와 시간. 생성 시점에 설정되며, 변경되지 않습니다.
     */
    private final LocalDateTime createdAt;

    /**
     * 그룹의 이름. 업데이트 가능하며, 비어 있지 않아야 합니다.
     */
    private String name;

    /**
     * 그룹의 상세 설명. 업데이트 가능하며, 비어 있지 않아야 합니다.
     */
    private String description;

    /**
     * 새로운 그룹을 생성하는 팩토리 메서드.
     * 그룹 ID는 null로 설정되며, 데이터베이스 삽입 시 자동 생성됩니다.
     *
     * @param name        그룹 이름. null 또는 공백일 수 없음.
     * @param category    그룹 카테고리. 유효한 Category 열거형 값이어야 함.
     * @param description 그룹 설명. null 또는 공백일 수 없음.
     * @return 새로 생성된 Group 인스턴스.
     * @throws DomainException 이름이나 설명이 비어 있으면 예외 발생.
     */
    public static Group create(String name,
                               Category category,
                               String description) {

        verifyParameters(name, category, description);
        return new Group(
                null,
                category,
                LocalDateTime.now(), // 데이터베이스에 저장될 때는 현재 시각을 사용함. (자동 생성)
                description,
                name
        );
    }

    private static void verifyParameters(String name, Category category, String description) {
        if (name == null || name.isBlank()) {
            throw new DomainException(ErrorCode.GROUP_NAME_IS_EMPTY);
        }
        if (description == null || description.isBlank()) {
            throw new DomainException(ErrorCode.GROUP_DESCRIPTION_IS_EMPTY);
        }
        if (category == null) {
            throw new DomainException(ErrorCode.CATEGORY_IS_EMPTY);
        }
    }

    /**
     * 기존 그룹 정보를 바탕으로 Group 인스턴스를 생성하는 팩토리 메서드.
     * 주로 데이터베이스 조회 결과를 매핑할 때 사용됩니다.
     *
     * @param groupId     그룹의 고유 ID.
     * @param name        그룹 이름.
     * @param category    그룹 카테고리.
     * @param description 그룹 설명.
     * @param createdAt   그룹 생성 시각.
     * @return 조회된 정보를 담은 Group 인스턴스.
     */
    public static Group of(Long groupId,
                           String name,
                           Category category,
                           String description,
                           LocalDateTime createdAt) {

        return new Group(groupId, category, createdAt, name, description);
    }

    // ==== 업데이트 로직 ==== //
    /**
     * 그룹 이름을 업데이트하는 메서드.
     * 새로운 이름이 기존 이름과 다를 경우에만 업데이트하며, null 또는 공백 값은 허용되지 않습니다.
     *
     * @param newName 새로운 그룹 이름.
     * @throws DomainException 새 이름이 null 또는 공백일 경우 예외 발생.
     */
    public void updateGroupName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new DomainException(ErrorCode.GROUP_NAME_IS_EMPTY);
        }

        if (!this.name.equals(newName)) {
            this.name = newName;
        }
    }

    /**
     * 그룹 설명을 업데이트하는 메서드.
     * 새로운 설명이 기존 설명과 다를 경우에만 업데이트하며, null 또는 공백 값은 허용되지 않습니다.
     *
     * @param newDescription 새로운 그룹 설명.
     * @throws DomainException 새 설명이 null 또는 공백일 경우 예외 발생.
     */
    public void updateDescription(String newDescription) {
        if (newDescription == null || newDescription.isBlank()) {
            throw new DomainException(ErrorCode.GROUP_DESCRIPTION_IS_EMPTY);
        }

        if (!this.description.equals(newDescription)) {
            this.description = newDescription;
        }
    }
}
