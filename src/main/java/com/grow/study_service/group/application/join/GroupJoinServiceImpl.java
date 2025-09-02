package com.grow.study_service.group.application.join;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.group.infra.persistence.repository.query.GroupQueryRepository;
import com.grow.study_service.group.presentation.dto.join.JoinInfoResponse;
import com.grow.study_service.group.presentation.dto.join.JoinRequest;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupJoinServiceImpl implements GroupJoinService {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupQueryRepository groupQueryRepository;
    private final GroupRepository groupRepository;
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

        // 그룹장에게 요청 전송 (redis 사용, 중복 요청을 거르기 위해서 set 구조 사용) - 같은 키값 사용 가능, value 만 다르면 됨
        String redisKey = getRedisKey(request.getGroupId()); // 키 생성

        // Sets에 추가하고 추가된 수 확인 (중복 요청 방지) - 값이 추가되면 1, 이미 존재하면 0 반환
        Long added = redisTemplate.opsForSet().add(redisKey, String.valueOf(memberId));

        if (added == 0L) {
            throw new ServiceException(ErrorCode.JOIN_REQUEST_ALREADY_SENT);
        }

        long expireTime = 7L * 24 * 60 * 60; // 7일 후에 초기화
        redisTemplate.expire(redisKey, expireTime, TimeUnit.SECONDS);

        log.info("[GROUP][JOIN][END] memberId={} groupId={} - 그룹 가입 요청 전송 완료", memberId, request.getGroupId());
    }

    /**
     * 주어진 회원 ID를 기반으로, 해당 회원이 그룹장(리더)인 그룹의 ID 리스트를 조회합니다.
     * 조회된 그룹 ID를 바탕으로 각 그룹의 이름을 추가하여 JoinInfoResponse 객체 리스트로 변환 후 반환합니다.
     * 그룹이 존재하지 않거나 이름 조회에 실패할 경우, 빈 리스트를 반환합니다.
     *
     * @param memberId 조회할 회원의 ID (필수, null 불가)
     * @return 그룹 ID와 그룹 이름을 포함하는 JoinInfoResponse 리스트 (빈 리스트일 수 있음)
     */
    @Override
    public List<JoinInfoResponse> findGroupIdsByLeaderId(Long memberId) {
        log.info("[GROUP][JOIN][START] memberId={} - 그룹 리스트 조회 시작", memberId);
        return groupQueryRepository.findGroupIdsByLeaderId(memberId).stream()
                .map(id -> {
                    String groupName = groupRepository.findGroupNameById(id);
                    return new JoinInfoResponse(id, groupName); // 그룹 정보 반환
                })
                .toList();
    }

    /**
     * 주어진 그룹 ID를 기반으로 Redis에서 해당 그룹의 가입 요청 멤버 ID 리스트를 조회합니다.
     * Redis Set 자료 구조를 사용하며, 키가 존재하지 않거나 멤버가 없을 경우 빈 리스트를 반환합니다.
     *
     * @param groupId 조회할 그룹의 ID (필수, null 불가)
     * @return 가입 요청 멤버 ID 리스트 (Long 타입, 빈 리스트일 수 있음)
     */
    @Override
    public List<Long> prepareFindJoinRequest(Long groupId) {
        // TODO 그룹장 권한 확인
        // Redis에서 해당 그룹의 가입 요청 멤버 ID 집합 조회
        String redisKey = getRedisKey(groupId);

        if (!redisTemplate.hasKey(redisKey)) {
            return List.of();  // 키가 없으면 빈 리스트 반환
        }

        Set<String> members = redisTemplate.opsForSet().members(redisKey);
        if (members == null || members.isEmpty()) {
            return List.of();  // 멤버가 없으면 빈 리스트 반환
        }

        // String 멤버 ID를 Long 리스트로 변환하여 반환
        return members.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private String getRedisKey(Long groupId) {
        return "group:" + groupId + ":send-joinRequests";
    }
}
