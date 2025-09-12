package com.grow.study_service.kanbanboard.application;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;
import com.grow.study_service.kanbanboard.domain.repository.KanbanBoardRepository;
import com.grow.study_service.kanbanboard.presentation.dto.TodoCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KanbanBoardServiceImpl implements KanbanBoardService {

    private final KanbanBoardRepository kanbanBoardRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * 새로운 TO-DO 항목을 생성합니다.
     * 주어진 회원 ID와 그룹 ID를 기반으로 그룹 멤버를 검증한 후, KanbanBoard 엔티티를 생성하고 저장합니다.
     *
     * @param memberId 회원 ID
     * @param groupId 그룹 ID
     * @param request TO-DO 생성 요청 객체 (내용, 시작 날짜, 종료 날짜 포함)
     * @return 생성된 TO-DO의 ID (KanbanId)
     * @throws ServiceException 그룹 멤버가 존재하지 않을 경우 GROUP_MEMBER_NOT_FOUND 오류 발생
     */
    @Override
    @Transactional
    public Long createTodo(Long memberId, Long groupId, TodoCreateRequest request) {
        log.info("[KanbanBoard][Create][START] 투두 생성 요청: memberId={}, groupId={}, request={}", memberId, groupId, request);

        // 회원 ID + 그룹 ID 를 기반으로 그룹 멤버를 조회
        // 해당 그룹에 가입된 회원인지 검증이 동시에 가능
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_MEMBER_NOT_FOUND));

        // 투두 생성
        KanbanBoard kanbanBoard = KanbanBoard.create(
                groupMember.getGroupMemberId(),
                request.getContent(),
                request.getStartDate(),
                request.getEndDate()
        );

        // 저장
        KanbanBoard saved = kanbanBoardRepository.save(kanbanBoard);

        log.info("[KanbanBoard][Create][END] 투두 생성 요청 처리 완료: kanbanBoard={}", saved.getKanbanId());

        return saved.getKanbanId();
    }
}
