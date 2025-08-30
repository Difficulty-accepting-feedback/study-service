package com.grow.study_service.group.application;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.group.presentation.dto.GroupDetailResponse;
import com.grow.study_service.group.presentation.dto.GroupResponse;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberApiService memberApiService;

    /**
     * 주어진 카테고리에 속한 모든 그룹을 조회하여 GroupResponse 리스트로 반환합니다.
     *
     * 이 메서드는 데이터베이스에서 지정된 카테고리의 그룹 목록을 가져온 후,
     * Stream API를 활용하여 GroupResponse DTO로 변환합니다. 각 그룹의 리더 정보를 조회하고,
     * WebClient를 통해 멤버 서비스에 동기 HTTP 요청을 보내 리더의 이름을 가져옵니다.
     *
     * <p><strong>통신 방식:</strong></p>
     * <ul>
     *   <li><strong>동기 통신(WebClient)</strong>: 멤버 서비스로부터 리더 이름을 즉시 조회</li>
     *   <li>이벤트 발행 대신 동기 통신을 선택한 이유: 화면 렌더링에 필요한 데이터를 즉시 가져와야 하므로</li>
     *   <li>실시간 데이터 조회가 필요하여 최종 일관성(Eventual Consistency)보다 즉시 일관성을 우선</li>
     * </ul>
     *
     * <p><strong>처리 과정:</strong></p>
     * <ol>
     *   <li>지정된 카테고리의 모든 그룹을 데이터베이스에서 조회</li>
     *   <li>각 그룹별로 리더(Role.LEADER) 정보를 조회</li>
     *   <li>WebClient를 통해 멤버 서비스에 동기 HTTP 요청을 전송하여 리더 이름 획득</li>
     *   <li>그룹 정보와 리더 이름을 결합하여 GroupResponse 객체 생성</li>
     *   <li>Stream API를 활용한 함수형 프로그래밍으로 변환 작업 수행</li>
     * </ol>
     *
     * 조회 과정은 읽기 전용 트랜잭션으로 실행되며, 로그를 통해 조회 시작과 종료를 기록합니다.
     *
     * @param category 조회할 그룹의 카테고리 (예: STUDY, HOBBY, MENTORING). null이 아니어야 합니다.
     * @return 지정된 카테고리의 그룹 정보와 리더 이름을 담은 GroupResponse 리스트. 그룹이 없을 경우 빈 리스트를 반환합니다.
     * @throws ServiceException 그룹에 리더가 존재하지 않거나 멤버 서비스 호출 실패 시 발생
     * @see GroupResponse#of(Group, String)
     * @see #createGroupResponse(Group)
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroupsByCategory(Category category) {
        log.info("[GROUP][SEARCH][START] 전체 {} 그룹 조회 시작", category.getDescription());

        List<GroupResponse> responses = groupRepository.findAllByCategory(category)
                .stream()
                .map(this::createGroupResponse)
                .collect(Collectors.toList());

        log.info("[GROUP][SEARCH][END] 전체 {} 그룹 조회 완료={} 건", category.getDescription(), responses.size());
        return responses;
    }

    /**
     * 주어진 그룹 ID에 해당하는 그룹의 상세 정보를 조회하여 GroupDetailResponse로 반환합니다.
     *
     * 이 메서드는 데이터베이스에서 그룹을 조회한 후, 조회수를 1 증가시키고 업데이트합니다.
     * 동시에 해당 그룹의 멤버 수를 계산하고, 그룹 리더의 이름을 멤버 서비스에서 가져와
     * 종합적인 그룹 상세 정보를 응답에 포함합니다.
     *
     * <p><strong>처리 과정:</strong></p>
     * <ol>
     *   <li>그룹 ID로 데이터베이스에서 그룹 정보 조회</li>
     *   <li>그룹 조회수 1 증가 후 데이터베이스에 업데이트</li>
     *   <li>해당 그룹의 총 멤버 수 계산</li>
     *   <li>그룹 리더(Role.LEADER) 정보 조회</li>
     *   <li>WebClient를 통해 멤버 서비스에 동기 HTTP 요청을 전송하여 리더 이름 획득</li>
     *   <li>그룹 정보, 멤버 수, 리더 이름을 결합하여 GroupDetailResponse 객체 생성</li>
     * </ol>
     *
     * 전체 과정은 트랜잭션으로 관리되며, 그룹이 존재하지 않거나 리더가 없을 경우 ServiceException을
     * 발생시킵니다. 로그를 통해 조회 시작과 종료를 기록합니다.
     *
     * @param groupId 조회할 그룹의 고유 ID. null이 아니어야 하며, 양의 정수여야 합니다.
     * @return 그룹 상세 정보, 멤버 수, 리더 이름을 담은 GroupDetailResponse 객체.
     * @throws ServiceException 그룹이 존재하지 않을 경우 (ErrorCode.GROUP_NOT_FOUND) 또는
     *                          그룹에 리더가 존재하지 않거나 멤버 서비스 호출 실패 시
     *                          (ErrorCode.GROUP_OR_READER_NOT_FOUND) 발생
     * @see GroupDetailResponse#of(Group, int, String)
     * @see #findAndIncrementViewCount(Long)
     * @see #getMemberCount(Long)
     * @see #getLeaderName(Group)
     */
    @Override
    @Transactional
    public GroupDetailResponse getGroup(Long groupId) {
        log.info("[GROUP][DETAIL][START] 그룹 상세 조회 시작 groupId={}", groupId);

        Group group = findAndIncrementViewCount(groupId);
        int memberCount = getMemberCount(groupId);
        String leaderName = getLeaderName(group);

        log.info("[GROUP][DETAIL][END] 그룹 상세 조회 완료 groupId={} memberCount={}", groupId, memberCount);

        return GroupDetailResponse.of(group, memberCount, leaderName);
    }

    private GroupResponse createGroupResponse(Group group) {
        // 그룹의 리더 찾기
        GroupMember groupLeader = groupMemberRepository.findByGroupIdAndLeader(group.getGroupId())
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_OR_LEADER_NOT_FOUND));

        String memberName = memberApiService.getMemberName(groupLeader.getMemberId());

        return GroupResponse.of(group, memberName);
    }

    private Group findAndIncrementViewCount(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_NOT_FOUND));

        Group updatedGroup = group.incrementViewCount();
        return groupRepository.save(updatedGroup);
    }

    private int getMemberCount(Long groupId) {
        return groupMemberRepository.findMemberCountByGroupId(groupId);
    }

    private String getLeaderName(Group group) {
        return groupMemberRepository.findByGroupIdAndLeader(group.getGroupId())
                .map(GroupMember::getMemberId)
                .map(memberApiService::getMemberName)
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_OR_LEADER_NOT_FOUND));
    }
}