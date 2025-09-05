package com.grow.study_service.group.application.join;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.common.util.JsonUtils;
import com.grow.study_service.group.application.event.GroupJoinRequestSentEvent;
import com.grow.study_service.group.application.event.NotificationType;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.group.infra.persistence.repository.query.GroupQueryRepository;
import com.grow.study_service.group.presentation.dto.join.JoinConfirmRequest;
import com.grow.study_service.group.presentation.dto.join.JoinInfoResponse;
import com.grow.study_service.group.presentation.dto.join.JoinRequest;
import com.grow.study_service.groupmember.domain.enums.Role;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 지정된 멤버를 지정된 그룹에 가입시킵니다.
     * <p>
     * 이 메서드는 먼저 해당 멤버가 이미 그룹에 가입되어 있는지 확인합니다. 이미 가입된 경우 {@link ServiceException}을 발생시킵니다.
     * 이후 결제 서비스로 결제 요청을 전송해야 하며 (현재 TODO로 표시됨), 성공 시 그룹 멤버를 저장합니다.
     *
     * @param memberId 가입할 멤버의 ID
     * @param groupId  가입할 그룹의 ID
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
     * <p>
     * 이 메서드는 먼저 해당 멤버가 이미 그룹에 가입되어 있는지 확인합니다. 이미 가입된 경우 {@link ServiceException}을 발생시킵니다.
     * 이후 Redis Set을 사용하여 중복 요청을 방지합니다. 이미 요청이 존재하면 예외를 발생시키고, 새로운 요청인 경우 Set에 추가한 후 7일 만료 시간을 설정합니다.
     * 요청 처리 전후에 로그를 기록합니다.
     *
     * @param request  그룹 가입 요청 객체 (groupId 포함)
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

        String groupName = groupRepository.findGroupNameById(request.getGroupId());

        // 그룹장에게 새로운 가입 신청이 왔다는 알림을 전송
        publishGroupJoinEvent(createRequestSentEvent(request, groupName));

        log.info("[GROUP][JOIN][END] memberId={} groupId={} - 그룹 가입 요청 전송 완료", memberId, request.getGroupId());
    }

    private GroupJoinRequestSentEvent createRequestSentEvent(JoinRequest request, String groupName) {
        return new GroupJoinRequestSentEvent(
                request.getLeaderId(),
                groupName + "에 새로운 가입 신청이 왔습니다. 어서 확인해 보세요!",
                NotificationType.GROUP_JOIN_REQUEST
        );
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

    /**
     * 그룹 가입 요청을 수락합니다.
     * 그룹 리더 권한을 확인한 후, 대상 멤버가 이미 그룹에 가입되어 있는지 검사합니다.
     * 이미 가입된 경우 예외를 발생시키고, 그렇지 않으면 새로운 그룹 멤버를 등록합니다.
     * 등록 후 Kafka 이벤트를 통해 상대방에게 수락 알림을 전송합니다. (TODO 구현 예정)
     *
     * @param memberId 현재 사용자의 멤버 ID (권한 확인용)
     * @param request 가입 수락 요청 정보 (그룹 ID와 대상 멤버 ID 포함)
     * @throws ServiceException 그룹 리더가 아닌 경우 (NO_PERMISSION_TO_ACCEPT_REQUEST),
     *                          이미 가입된 경우 (ALREADY_ACCEPTED_REQUEST)
     */
    @Override
    @Transactional
    public void acceptJoinRequest(Long memberId, JoinConfirmRequest request) {
        // memberId = 그룹장 아이디, request.getMemberId = 대상 멤버 아이디
        verifyGroupLeaderPermission(memberId, request);

        // 이미 수락한 요청인지 확인
        if (groupMemberRepository.existsByMemberIdAndGroupId(request.getMemberId(), request.getGroupId())) {
            throw new ServiceException(ErrorCode.ALREADY_ACCEPTED_REQUEST);
        }

        String groupName = groupRepository.findGroupNameById(request.getGroupId());

        // groupMember 에 등록
        groupMemberRepository.save(
                GroupMember.create(
                        request.getMemberId(),
                        request.getGroupId(),
                        Role.MEMBER)
        );

        // redis 에서 요청 삭제하기 (승인 후 거절이 되지 않도록)
        removeJoinRequestFromRedis(request.getGroupId(), request.getMemberId());

        // 그룹에 가입 요청을 보낸 유저에게 수락 알림 전송
        publishGroupJoinEvent(createRequestApprovalEvent(request.getMemberId(), groupName));
    }

    private GroupJoinRequestSentEvent createRequestApprovalEvent(Long memberId, String groupName) {
        return new GroupJoinRequestSentEvent(
                memberId,
                groupName + "에서 신청을 수락했습니다. 어서 확인해 보세요!",
                NotificationType.GROUP_JOIN_APPROVAL
        );
    }

    /**
     * 그룹 가입 요청을 거절합니다.
     * 그룹 리더 권한을 확인한 후, 대상 멤버를 등록하지 않습니다.
     * 거절 후 Kafka 이벤트를 통해 상대방에게 거절 알림을 전송합니다. (TODO 구현 예정)
     *
     * @param memberId 현재 사용자의 멤버 ID (권한 확인용)
     * @param request 가입 거절 요청 정보 (그룹 ID와 대상 멤버 ID 포함)
     * @throws ServiceException 그룹 리더가 아닌 경우 (NO_PERMISSION_TO_ACCEPT_REQUEST)
     */
    @Override
    @Transactional(readOnly = true)
    public void rejectJoinRequest(Long memberId, JoinConfirmRequest request) {
        verifyGroupLeaderPermission(memberId, request);

        String groupName = groupRepository.findGroupNameById(request.getGroupId());

        // redis 에서 요청 삭제하기 (거절 후 승인이 되지 않도록)
        removeJoinRequestFromRedis(request.getGroupId(), request.getMemberId());

        // 등록하지 않음, 그룹에 가입 요청을 보낸 유저에게 거절 알림 전송
        publishGroupJoinEvent(createRequestRejectionEvent(request.getMemberId(), groupName));
    }

    private GroupJoinRequestSentEvent createRequestRejectionEvent(Long memberId, String groupName) {
        return new GroupJoinRequestSentEvent(
                memberId,
                // 뭐 어떻게 위로해 줘야 할지... 그냥 이렇게만 보여 주면 되나... 다음 기회를 노리라고 하면 놀리는 거 같잖아요
                groupName + "에서 신청을 거절했습니다.",
                NotificationType.GROUP_JOIN_REJECTION
        );
    }

    private void publishGroupJoinEvent(GroupJoinRequestSentEvent sentEvent) {
        kafkaTemplate.send(
                "group.join-request.notification",
                JsonUtils.toJsonString(sentEvent) // 알림 이벤트를 JSON 형태로 변환한 문자열로 전송
        );

        log.debug("[KAFKA][SENT] message={}", sentEvent.getMessage());
    }

    private void removeJoinRequestFromRedis(Long groupId, Long memberId) {
        String key = getRedisKey(groupId);
        String memberValue = String.valueOf(memberId);

        // 1. 해당 키가 존재하는지 확인 - 그룹에 참여 요청이 도착하지 않았으면 키가 생성되지 않음
        Boolean keyExists = redisTemplate.hasKey(key);
        if (!keyExists || keyExists == null) {
            throw new ServiceException(ErrorCode.JOIN_REQUEST_NOT_FOUND);
        }

        // 2. 해당 키에 해당하는 멤버가 존재하는지 확인 - 그룹에 해당 멤버가 참여 요청을 보내지 않았으면 키에 값이 없음
        Boolean memberExists = redisTemplate.opsForSet().isMember(key, memberValue);
        if (!memberExists || memberExists == null) {
            throw new ServiceException(ErrorCode.JOIN_REQUEST_MEMBER_NOT_FOUND);
        }

        // 3. redis 에서 삭제 수행
        Long removedCount = redisTemplate.opsForSet().remove(key, memberValue);

        // 3-1. 삭제 수행이 실패한 경우 - 이미 처리된 대상
        if (removedCount == 0L || removedCount == null) {
            log.warn("[REDIS][REMOVE] key={} memberId={} - 그룹에 참여 요청이 삭제되지 않았습니다.", key, memberId);
            throw new ServiceException(ErrorCode.JOIN_REQUEST_ALREADY_REMOVED);
        }

        log.debug("[REDIS][REMOVE] key={} memberId={} - 그룹에 참여 요청이 삭제되었습니다.", key, memberId);
    }

    private void verifyGroupLeaderPermission(Long memberId, JoinConfirmRequest request) {
        // 권한이 있는지 확인 - 리더만 요청을 수락 / 거절할 수 있다
        if (!groupMemberRepository.isLeader(request.getGroupId(), memberId)) {
            throw new ServiceException(ErrorCode.NO_PERMISSION_TO_ACCEPT_REQUEST);
        }
    }

    private String getRedisKey(Long groupId) {
        return "group:" + groupId + ":send-joinRequests";
    }
}