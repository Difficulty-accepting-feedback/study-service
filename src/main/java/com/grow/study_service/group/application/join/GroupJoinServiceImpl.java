package com.grow.study_service.group.application.join;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.presentation.dto.JoinRequest;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupJoinServiceImpl implements GroupJoinService {

    private final GroupMemberRepository groupMemberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 지정된 멤버를 지정된 그룹에 가입시킵니다.
     *
     * 이 메서드는 먼저 해당 멤버가 이미 그룹에 가입되어 있는지 확인합니다. 이미 가입된 경우 {@link ServiceException}을 발생시킵니다.
     * 이후 결제 서비스로 결제 요청을 전송해야 하며 (현재 TODO로 표시됨), 성공 시 그룹 멤버를 저장합니다.
     *
     * @param memberId 가입할 멤버의 ID
     * @param groupId 가입할 그룹의 ID
     * @throws ServiceException 그룹에 이미 가입된 경우 ({@link ErrorCode#GROUP_ALREADY_JOINED})
     */
    @Override
    @Transactional
    public void joinGroup(Long memberId, Long groupId) {
        log.info("[GROUP][JOIN][START] memberId={} groupId={} - 그룹 가입 시작", memberId, groupId);

        // 중복 가입 제거
        if (groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId)) {
            throw new ServiceException(ErrorCode.GROUP_ALREADY_JOINED);
        }

        // TODO 결제 서비스로 결제 요청 전송 필요

        // 그룹에 멤버 추가
        groupMemberRepository.save(GroupMember.create(memberId, groupId, Role.MEMBER));

        log.info("[GROUP][JOIN][END] memberId={} groupId={} - 그룹 가입 완료", memberId, groupId);
    }

    /**
     * 지정된 멤버가 그룹 가입 요청을 전송합니다.
     *
     * 이 메서드는 먼저 해당 멤버가 이미 그룹에 가입되어 있는지 확인합니다. 이미 가입된 경우 {@link ServiceException}을 발생시킵니다.
     * 이후 Redis Set을 사용하여 중복 요청을 방지합니다. 이미 요청이 존재하면 예외를 발생시키고, 새로운 요청인 경우 Set에 추가한 후 7일 만료 시간을 설정합니다.
     * 요청 처리 전후에 로그를 기록합니다.
     *
     * @param request 그룹 가입 요청 객체 (groupId 포함)
     * @param memberId 요청을 보내는 멤버의 ID
     * @throws ServiceException 그룹에 이미 가입된 경우 ({@link ErrorCode#GROUP_ALREADY_JOINED}) 또는 이미 가입 요청이 전송된 경우 ({@link ErrorCode#JOIN_REQUEST_ALREADY_SENT})
     */
    @Override
    @Transactional(readOnly = true)
    public void sendJoinRequest(JoinRequest request, Long memberId) {
        log.info("[GROUP][JOIN][START] memberId={} groupId={} - 그룹 가입 요청 전송 시작", memberId, request.getGroupId());

        // 중복 가입 제거
        if (groupMemberRepository.existsByGroupIdAndMemberId(request.getGroupId(), memberId)) {
            throw new ServiceException(ErrorCode.GROUP_ALREADY_JOINED);
        }

        // 그룹장에게 요청 전송 (redis 사용, 중복 요청을 거르기 위해서 set 구조 사용)
        String redisKey = "group:" + request.getGroupId() + ":join-joinRequests"; // 키 생성

        // Sets에 추가하고 추가된 수 확인 (중복 요청 방지) - 값이 추가되면 1, 이미 존재하면 0 반환
        Long added = redisTemplate.opsForSet().add(redisKey, String.valueOf(memberId));

        if (added == 0L) {
            throw new ServiceException(ErrorCode.JOIN_REQUEST_ALREADY_SENT);
        }

        long expireTime = 7L * 24 * 60 * 60; // 7일 후에 초기화
        redisTemplate.expire(redisKey, expireTime, TimeUnit.SECONDS);

        log.info("[GROUP][JOIN][END] memberId={} groupId={} - 그룹 가입 요청 전송 완료", memberId, request.getGroupId());
    }
}
