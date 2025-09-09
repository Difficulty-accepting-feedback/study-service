package com.grow.study_service.groupmember.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.groupmember.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 그룹 멤버 도메인 모델 클래스.
 * 이 클래스는 스터디 그룹의 멤버 정보를 관리하며, 멤버 생성, 조회, 역할 업데이트 기능을 제공합니다.
 * 불변 필드(Immutable fields)를 사용하여 데이터의 안정성을 보장합니다.
 *
 * @author sun
 * @version 1.0
 * @since 2025-08-07
 */
@Getter
@AllArgsConstructor
public class GroupMember {

	/**
	 * 그룹 멤버의 고유 식별자(ID). 데이터베이스에서 자동 생성되며, null일 수 있습니다.
	 */
	private final Long groupMemberId;

	/**
	 * 멤버의 고유 식별자(ID). 그룹에 속한 사용자의 ID입니다.
	 */
	private final Long memberId;

	/**
	 * 그룹의 고유 식별자(ID). 이 멤버가 속한 그룹의 ID입니다.
	 */
	private final Long groupId;

	/**
	 * 멤버의 역할. 업데이트 가능하며, Role 열거형 값으로 관리됩니다.
	 */
	private Role role;

	/**
	 * 멤버가 그룹에 가입한 날짜와 시간. 생성 시점에 설정되며, 변경되지 않습니다.
	 */
	private final LocalDateTime joinedAt;

    private int totalAttendanceDays; // 누적 출석일 카운트

    private Long version;

    /**
     * 새로운 그룹 멤버를 생성하는 팩토리 메서드.
     * 그룹 멤버 ID는 null로 설정되며, 데이터베이스 삽입 시 자동 생성됩니다.
     *
     * @param memberId 멤버 ID. null 또는 0 이하일 수 없음.
     * @param groupId  그룹 ID. null 또는 0 이하일 수 없음.
     * @param role     멤버 역할. null일 수 없음.
     * @return 새로 생성된 GroupMember 인스턴스.
     * @throws DomainException 매개변수가 유효하지 않으면 예외 발생.
     */
    public static GroupMember create(Long memberId,
                                     Long groupId,
                                     Role role) {

        verifyParameters(memberId, groupId, role);
        return new GroupMember(
                null,
                memberId,
                groupId,
                role,
                LocalDateTime.now(), // 데이터베이스에 저장될 때는 현재 시각을 사용함. (자동 생성)
                0,
                null // 버전 자동 생성
        );
    }

    /**
     * 기존 그룹 멤버 정보를 바탕으로 GroupMember 인스턴스를 생성하는 팩토리 메서드.
     * 주로 데이터베이스 조회 결과를 매핑할 때 사용됩니다.
     *
     * @param groupMemberId 그룹 멤버 ID.
     * @param memberId      멤버 ID.
     * @param groupId       그룹 ID.
     * @param role          멤버 역할.
     * @param joinedAt      가입 시각.
     * @return 조회된 정보를 담은 GroupMember 인스턴스.
     * @throws DomainException 매개변수가 유효하지 않으면 예외 발생.
     */
    public static GroupMember of(Long groupMemberId,
                                 Long memberId,
                                 Long groupId,
                                 Role role,
                                 LocalDateTime joinedAt,
                                 int totalAttendanceDays,
                                 Long version) {

        verifyParameters(memberId, groupId, role);
        verifyIdAndJoinedAt(groupMemberId, joinedAt);

        return new GroupMember(
                groupMemberId,
                memberId,
                groupId,
                role,
                joinedAt,
                totalAttendanceDays,
                version
        );
    }

    /**
     * 매개변수의 유효성을 검사하는 private 메서드.
     * 생성 또는 조회 시 매개변수가 적절한지 확인합니다.
     *
     * @param memberId 멤버 ID.
     * @param groupId  그룹 ID.
     * @param role     멤버 역할.
     * @throws DomainException 매개변수가 null 또는 유효하지 않으면 예외 발생.
     */
    private static void verifyParameters(Long memberId,
                                         Long groupId,
                                         Role role) {

        if (memberId == null || memberId <= 0L) {
            throw new DomainException(ErrorCode.GROUP_MEMBER_MEMBER_ID_IS_EMPTY);
        }

        if (groupId == null || groupId <= 0L) {
            throw new DomainException(ErrorCode.GROUP_MEMBER_GROUP_ID_IS_EMPTY);
        }

        if (role == null) {
            throw new DomainException(ErrorCode.GROUP_MEMBER_ROLE_IS_EMPTY);
        }
    }

    /**
     * ID와 가입 시각의 유효성을 검사하는 private 메서드.
     * 조회 시 ID와 joinedAt가 적절한지 확인합니다.
     *
     * @param groupMemberId 그룹 멤버 ID.
     * @param joinedAt      가입 시각.
     * @throws DomainException 매개변수가 null 또는 유효하지 않으면 예외 발생.
     */
    private static void verifyIdAndJoinedAt(Long groupMemberId, LocalDateTime joinedAt) {
        if (joinedAt == null) {
            throw new DomainException(ErrorCode.GROUP_MEMBER_JOINED_AT_IS_EMPTY);
        }

        if (groupMemberId == null || groupMemberId <= 0L) {
            throw new DomainException(ErrorCode.GROUP_MEMBER_ID_IS_EMPTY);
        }
    }

    // ==== 업데이트 메서드 ==== //

    /**
     * 멤버 역할을 업데이트하는 메서드.
     * 새로운 역할이 기존 역할과 다를 경우에만 업데이트하며, null 값은 허용되지 않습니다.
     *
     * @param newRole 새로운 멤버 역할.
     * @throws DomainException 새 역할이 null일 경우 예외 발생.
     */
    public void updateRole(Role newRole) {
        if (newRole == null) {
            throw new DomainException(ErrorCode.GROUP_MEMBER_ROLE_IS_EMPTY);
        }

        if (!this.role.equals(newRole)) {
            this.role = newRole;
        }
    }

    /**
     * 그룹 리더인지 여부를 검증하는 메서드.
     * <p>
     * 전달받은 {@link Role}이 {@code LEADER}가 아닐 경우
     * {@link DomainException}을 발생시켜 호출 흐름을 중단한다.
     * </p>
     *
     * <p>이 메서드는 주로 그룹장 권한이 필요한 서비스 로직에서
     * 사전 권한 체크용으로 사용된다.</p>
     *
     * @param role 검증할 사용자의 역할
     * @throws DomainException 사용자의 역할이 {@code LEADER}가 아닐 경우
     *                         {@link ErrorCode#GROUP_LEADER_REQUIRED} 코드와 함께 예외가 발생한다.
     */
    public void verifyGroupLeader(Role role) {
        if (role != Role.LEADER) {
            throw new DomainException(ErrorCode.GROUP_LEADER_REQUIRED);
        }
    }

    /**
     * 출석일 증가 메서드
     */
    public GroupMember incrementAttendanceDays() {
        this.totalAttendanceDays++;
        return this;
    }

    /**
     * 출석률 계산 메서드
     *
     * @param totalDays 출석일 수 (총 출석일 수)
     * @return 출석률 (0 ~ 100), 소수점 첫 자리에서 반올림.
     */
    public double calculateTotalAttendanceRate(long totalDays) {
        return (this.totalAttendanceDays / (double) totalDays) * 100;
    }
}
