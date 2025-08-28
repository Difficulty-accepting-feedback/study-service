package com.grow.study_service.group.application;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.model.Group;
import com.grow.study_service.group.domain.repository.GroupRepository;
import com.grow.study_service.group.presentation.dto.GroupDetailResponse;
import com.grow.study_service.group.presentation.dto.GroupResponse;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * 주어진 카테고리에 속한 모든 그룹을 조회하여 GroupResponse 리스트로 반환합니다.
     *
     * 이 메서드는 데이터베이스에서 지정된 카테고리의 그룹 목록을 가져온 후,
     * 이를 GroupResponse DTO로 변환합니다. 조회 과정은 읽기 전용 트랜잭션으로 실행되며,
     * 로그를 통해 조회 시작과 종료를 기록합니다.
     *
     * @param category 조회할 그룹의 카테고리 (예: STUDY, HOBBY, MENTORING). null이 아니어야 합니다.
     * @return 지정된 카테고리의 그룹 정보를 담은 GroupResponse 리스트. 그룹이 없을 경우 빈 리스트를 반환합니다.
     * @since 1.0
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroupsByCategory(Category category) {
        log.info("[GROUP][SEARCH][START] 전체 {} 그룹 조회 시작", category.getDescription());

        List<Group> groupList = groupRepository.findAllByCategory(category);

        List<GroupResponse> list = groupList.stream()
                .map(GroupResponse::of)
                .collect(Collectors.toList());

        log.info("[GROUP][SEARCH][END] 전체 {} 그룹 조회 완료={} 건", category.getDescription(), list.size());
        return list;
    }

    /**
     * 주어진 그룹 ID에 해당하는 그룹의 상세 정보를 조회하여 GroupDetailResponse로 반환합니다.
     *
     * 이 메서드는 데이터베이스에서 그룹을 조회한 후, 조회수를 1 증가시키고 업데이트합니다.
     * 동시에 해당 그룹의 멤버 수를 계산하여 응답에 포함합니다. 전체 과정은 트랜잭션으로 관리되며,
     * 그룹이 존재하지 않을 경우 ServiceException을 발생시킵니다. 로그를 통해 조회 시작과 종료를 기록합니다.
     *
     * @param groupId 조회할 그룹의 고유 ID. null이 아니어야 하며, 양의 정수여야 합니다.
     * @return 그룹 상세 정보와 멤버 수를 담은 GroupDetailResponse 객체.
     * @throws ServiceException 그룹이 존재하지 않을 경우 (ErrorCode.GROUP_NOT_FOUND) 발생.
     */
    @Override
    @Transactional
    public GroupDetailResponse getGroup(Long groupId) {
        log.info("[GROUP][DETAIL][START] 그룹 상세 조회 시작 groupId={}", groupId);

        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new ServiceException(ErrorCode.GROUP_NOT_FOUND));

        Group updated = group.incrementViewCount();// 조회수 증가

        groupRepository.save(updated); // 조회수 업데이트

        int memberCount = groupMemberRepository.findMemberCountByGroupId(groupId); // 이것도 그룹의 필드로 저장할까 고민 중입니다... 흠

        log.info("[GROUP][DETAIL][END] 그룹 상세 조회 완료 groupId={} memberCount={}", groupId, memberCount);

        return GroupDetailResponse.of(group, memberCount);
    }
}