package com.grow.study_service.kanbanboard.application;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.groupmember.domain.model.GroupMember;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.kanbanboard.domain.model.KanbanBoard;
import com.grow.study_service.kanbanboard.domain.repository.KanbanBoardRepository;
import com.grow.study_service.kanbanboard.presentation.dto.TodoCreateRequest;
import com.grow.study_service.kanbanboard.presentation.dto.response.KanbanBoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 주어진 회원 ID와 그룹 ID를 기반으로 해당 그룹의 TO-DO 목록을 조회합니다.
     * 회원의 그룹 가입 여부를 검증한 후, 현재 날짜부터 한 달 후까지의 KanbanBoard 목록을 startDate 오름차순으로 반환합니다.
     *
     * @param memberId 회원 ID
     * @param groupId 그룹 ID
     * @return 해당 범위 내 KanbanBoardResponse 목록
     * @throws ServiceException 그룹 멤버가 존재하지 않을 경우 GROUP_MEMBER_NOT_FOUND 오류 발생
     */
    @Override
    @Transactional(readOnly = true)
    public List<KanbanBoardResponse> getTodos(Long memberId, Long groupId) {
        // 회원 ID + 그룹 ID 를 기반으로 그룹 멤버를 조회
        // 해당 그룹에 가입된 회원인지 검증이 동시에 가능
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new ServiceException(ErrorCode.GROUP_MEMBER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        // 해당 그룹 멤버를 기준으로 한 달 기준의 투두 목록을 조회 (오늘로부터 한 달 뒤까지)
        List<KanbanBoard> list = kanbanBoardRepository.findByGroupMemberIdAndDateBetween(groupMember.getGroupMemberId(),
                now,
                now.plusMonths(1)
        );

        return list.stream()
                .map(KanbanBoardResponse::of)
                .toList();
    }

    /**
     * TO-DO 항목을 업데이트합니다.
     * 주어진 kanbanId로 TO-DO를 조회한 후, 요청된 내용으로 업데이트하고 저장합니다.
     * 업데이트는 도메인 로직에 위임되며, 이미 완료된 TO-DO는 변경할 수 없습니다.
     *
     * @param memberId 회원 ID (현재 사용되지 않음, 추후 권한 검증 등에 사용 가능)
     * @param request TO-DO 업데이트 요청 객체 (내용, 시작 날짜, 종료 날짜, 상태 포함)
     * @param kanbanId 업데이트할 TO-DO의 ID
     * @return 업데이트된 TO-DO의 ID
     * @throws ServiceException TO-DO가 존재하지 않을 경우 TODO_NOT_FOUND 오류 발생
     * @throws DomainException 이미 완료된 TO-DO를 변경하려 할 경우 CANNOT_CHANGE_STATUS_OF_COMPLETED_TODO 오류 발생
     */
    @Override
    @Transactional
    public Long updateTodo(Long memberId, TodoCreateRequest request, Long kanbanId) {
        // 할 일 조회
        KanbanBoard findTodo = kanbanBoardRepository.findById(kanbanId).orElseThrow(() ->
                new ServiceException(ErrorCode.TODO_NOT_FOUND));

        // 내용, 날짜, 상태 변경 - 도메인 로직에 위임
        findTodo.updateTodo(request);

        // 새롭게 업데이트
        KanbanBoard saved = kanbanBoardRepository.save(findTodo);

        return saved.getKanbanId();
    }
}
