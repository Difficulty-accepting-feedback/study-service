package com.grow.study_service.group.application;

import com.grow.study_service.group.application.api.MemberApiService;
import com.grow.study_service.group.application.dto.GroupDetailPrep;
import com.grow.study_service.group.application.dto.GroupWithLeader;
import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.presentation.dto.GroupDetailResponse;
import com.grow.study_service.group.presentation.dto.GroupResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GroupMainService는 파사드 패턴을 적용하여
 * 트랜잭션 처리 부분 (GroupService)과 외부 API 호출 부분 (MemberApiService)을 구분하고 조합합니다.
 * 이 클래스는 클라이언트에게 단순화된 인터페이스를 제공하며,
 * 내부적으로 트랜잭션과 API 호출을 분리하여 관리합니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupFacadeService {

    private final GroupTransactionService groupTransactionService; // 트랜잭션 처리 담당
    private final MemberApiService memberApiService; // 외부 API 호출 담당

    public List<GroupResponse> getAllGroupsByCategory(Category category) {
        // 트랜잭션 내에서 그룹 + 리더의 데이터를 가져온 후,
        List<GroupWithLeader> groupsWithLeaders = groupTransactionService.prepareGroupsByCategory(category);

        // 각 그룹의 리더 정보를 조회하고,
        List<Long> memberIds = groupsWithLeaders.stream()
                .map(gwl -> gwl.getLeader().getMemberId())
                .collect(Collectors.toList());

        // WebClient를 통해 멤버 서비스에 동기 HTTP 요청을 보내 리더의 이름을 가져옴
        List<String> leaderNames = memberApiService.fetchMemberNames(memberIds);

        // 그룹 정보와 리더 이름을 결합하여 GroupResponse 객체 생성
        return groupTransactionService.buildGroupResponses(groupsWithLeaders, leaderNames, category);
    }

    public GroupDetailResponse getGroupByCategory(Long groupId) {
        // 트랜잭션 내에서 그룹 데이터 가져온 후, API 호출으로 멤버 정보 보강
        GroupDetailPrep detailPrep = groupTransactionService.prepareGroupDetail(groupId);
        // 멤버 이름 가져오기 (API 호출)
        String memberName = memberApiService.getMemberName(detailPrep.getLeaderId());
        // 그룹 상세 정보 생성 (DTO 생성)
        return groupTransactionService.buildGroupDetailResponse(detailPrep, memberName, groupId);
    }
}
