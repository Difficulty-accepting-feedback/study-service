package com.grow.study_service.dashboard.application.impl;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.dashboard.application.DashboardService;
import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;

    /**
     * 주어진 그룹 ID와 멤버 ID에 해당하는 멤버의 누적 출석 일수를 증가시킵니다.
     *
     * @param groupId 그룹 ID (검증용)
     * @param memberId 멤버 ID
     * @throws DomainException 멤버가 존재하지 않거나 그룹에 속하지 않을 경우
     */
    @Override
    @Transactional
    public void incrementAttendanceDays(Long groupId, Long memberId) {
        GroupMember findMember = groupMemberRepository.findById(memberId).orElseThrow(() ->
                new DomainException(ErrorCode.GROUP_MEMBER_NOT_FOUND));

        log.info("[DASHBOARD][ATTENDANCE][START] 그룹 {}의 멤버 {}의 출석일을 누적 시작, 현재 출석일={}",
                groupId, memberId, findMember.getTotalAttendanceDays());

        // 도메인 로직 호출: 출석 일수 누적 업데이트 (반환값으로 업데이트된 도메인 객체 받음)
        // 반환값이 필요는 없으나, 로그에서 편하게 확인하기 위해서 반환값 사용
        GroupMember updated = findMember.incrementAttendanceDays();

        groupMemberRepository.save(updated);

        log.info("[DASHBOARD][ATTENDANCE][END] 그룹 {}의 멤버 {}의 출석일을 누적 완료, 현재 출석일={}",
                groupId, memberId, updated.getTotalAttendanceDays());
    }

    /**
     * 그룹과 멤버의 출석 정보를 이용해 출석률을 계산합니다.
     * 출석률 = (member.totalAttendanceDays / 총 기간(일)) * 100
     *
     * @param groupId  그룹 ID
     * @param memberId 멤버 ID
     * @return 출석률(퍼센트)
     * @throws ServiceException 그룹/멤버 없음
     * @throws DomainException  날짜 범위 오류 시
     */
    @Override
    @Transactional(readOnly = true)
    public Double getAttendanceRate(Long groupId, Long memberId) {
        // 유지보수를 위해서 전체 조회를 기본으로 -> 성능 병목이 생길 경우에는 부분 조회로 변경 (유지보수성과 성능의 트레이드오프로 결정)
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new ServiceException(ErrorCode.GROUP_NOT_FOUND));

        GroupMember groupMember = groupMemberRepository.findById(memberId).orElseThrow(() ->
                new ServiceException(ErrorCode.GROUP_MEMBER_NOT_FOUND));

        LocalDateTime start = group.getStartAt();  // 시작일
        LocalDateTime end = group.getEndAt();      // 종료일
        int totalAttendanceDays = groupMember.getTotalAttendanceDays(); // 총 출석 일수

        double totalDays = group.calculateTotalAttendanceDays(start, end); // 총 일수 계산 (시작일 포함)
        double rate = (totalAttendanceDays / totalDays) * 100;

        return Math.round(rate * 10) / 10.0;
    }
}