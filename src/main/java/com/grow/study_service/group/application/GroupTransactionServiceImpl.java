package com.grow.study_service.group.application;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.application.dto.GroupDetailPrep;
import com.grow.study_service.group.application.dto.GroupWithLeader;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.group.infra.persistence.repository.query.GroupQueryRepository;
import com.grow.study_service.group.presentation.dto.GroupDetailResponse;
import com.grow.study_service.group.presentation.dto.GroupResponse;
import com.grow.study_service.group.presentation.dto.GroupSimpleResponse;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupTransactionServiceImpl implements GroupTransactionService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupQueryRepository groupQueryRepository;

    /**
     * 주어진 카테고리에 해당하는 모든 그룹과 각각의 그룹 리더 정보를 조회하여
     * GroupWithLeader 객체 리스트로 반환합니다.
     *
     * 이 메서드는 트랜잭션(readOnly) 범위 내에서 동작하며,
     * 1) 지정된 카테고리의 그룹 리스트를 데이터베이스에서 가져오고,
     * 2) 각 그룹별 리더 멤버 정보를 조회하여 이를 함께 묶어 반환하는 역할을 합니다.
     *
     * @param category 조회할 그룹의 카테고리
     * @return 지정된 카테고리의 그룹과 그룹 리더 정보를 포함하는 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupWithLeader> prepareGroupsByCategory(Category category) {
        log.info("[GROUP][SEARCH][START] 전체 {} 그룹 조회 시작", category.getDescription());

        List<Group> groupList = getAllByCategory(category);

        List<GroupWithLeader> groupsWithLeaders = new ArrayList<>();
        for (Group group : groupList) {
            GroupMember groupLeader = getGroupLeader(group);
            groupsWithLeaders.add(new GroupWithLeader(group, groupLeader));
        }

        return groupsWithLeaders;
    }

    private List<Group> getAllByCategory(Category category) {
        return groupRepository.findAllByCategory(category);
    }

    private GroupMember getGroupLeader(Group group) {
        return groupMemberRepository.findByGroupIdAndLeader(group.getGroupId())
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_OR_LEADER_NOT_FOUND));
    }

    /**
     * 그룹 정보와 해당 그룹 리더의 이름을 결합하여 GroupResponse 리스트를 생성합니다.
     *
     * 각 GroupWithLeader와 멤버 이름 리스트의 인덱스를 매칭하여
     * GroupResponse DTO를 만들어 반환합니다.
     *
     * @param groupsWithLeaders 그룹과 각 그룹 리더 정보를 포함한 리스트
     * @param memberNames 각 그룹 리더 멤버의 이름 리스트(인덱스 순서가 groupsWithLeaders와 일치)
     * @param category 조회한 그룹들의 카테고리 정보
     * @return 그룹과 리더 이름이 결합된 GroupResponse 객체 리스트
     */
    @Override
    public List<GroupResponse> buildGroupResponses(List<GroupWithLeader> groupsWithLeaders,
                                                   List<String> memberNames, Category category) {
        ArrayList<GroupResponse> responses = new ArrayList<>();
        for (int i = 0; i < groupsWithLeaders.size(); i++) {
            Group group = groupsWithLeaders.get(i).getGroup();
            String memberName = memberNames.get(i);

            GroupResponse response = GroupResponse.of(group, memberName);
            responses.add(response);
        }

        log.info("[GROUP][SEARCH][END] 전체 {} 그룹 조회 완료={} 건", category.getDescription(), responses.size());
        return responses;
    }

    /**
     * 특정 그룹의 상세 정보를 준비합니다.
     *
     * 1) 그룹 조회 및 조회수 증가를 수행하고,
     * 2) 해당 그룹의 멤버 수를 조회하며,
     * 3) 그룹 리더의 멤버 ID를 확인합니다.
     *
     * 모든 작업은 트랜잭션 내에서 수행되어 데이터 일관성을 보장합니다.
     *
     * @param groupId 상세 조회할 그룹의 ID
     * @return 조회된 그룹 정보, 멤버 수, 리더 ID를 포함한 GroupDetailPrep 객체
     * @throws ServiceException 그룹을 찾지 못하거나 리더 정보가 없는 경우 발생
     */
    @Override
    @Transactional
    public GroupDetailPrep prepareGroupDetail(Long groupId) {
        log.info("[GROUP][DETAIL][START] 그룹 상세 조회 시작 groupId={}", groupId);

        Group group = findAndIncrementViewCount(groupId);
        int memberCount = getMemberCount(groupId);

        Long leaderId = groupMemberRepository.findByGroupIdAndLeader(group.getGroupId())
                .map(GroupMember::getMemberId)
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_OR_LEADER_NOT_FOUND));

        return new GroupDetailPrep(group, memberCount, leaderId);
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

    /**
     * 그룹 상세 조회 결과 DTO를 생성하여 반환합니다.
     *
     * 주어진 GroupDetailPrep 객체에서 그룹 정보와 멤버 수를 꺼내고,
     * 별도로 조회한 리더 이름과 결합하여 GroupDetailResponse 객체를 만듭니다.
     *
     * 메서드 실행 완료 시 로그를 남깁니다.
     *
     * @param prep 그룹 정보, 멤버 수, 리더 ID가 포함된 GroupDetailPrep 객체
     * @param leaderName 리더의 이름
     * @param groupId 조회 대상 그룹 ID (로그용)
     * @return 그룹 상세 조회 결과를 담은 GroupDetailResponse DTO 객체
     */
    @Override
    public GroupDetailResponse buildGroupDetailResponse(GroupDetailPrep prep, String leaderName, Long groupId) {
        Group group = prep.getGroup();
        int memberCount = prep.getMemberCount();

        log.info("[GROUP][DETAIL][END] 그룹 상세 조회 완료 groupId={} memberCount={}", groupId, memberCount);

        return GroupDetailResponse.of(group, memberCount, leaderName);
    }

    // 빈 리스트 반환 가능
    @Override
    @Transactional(readOnly = true)
    public List<GroupSimpleResponse> getMyGroupsByCategory(Category category, Long memberId) {
        return groupQueryRepository.findJoinedGroupsByMemberAndCategory(memberId, category);
    }
}