package com.grow.study_service.dashboard.application.impl;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.dashboard.application.DashboardService;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final GroupMemberRepository groupMemberRepository;

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
}