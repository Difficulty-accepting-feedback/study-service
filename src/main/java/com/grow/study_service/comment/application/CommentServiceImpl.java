package com.grow.study_service.comment.application;

import com.grow.study_service.comment.domain.model.Comment;
import com.grow.study_service.comment.domain.repository.CommentRepository;
import com.grow.study_service.comment.presentation.dto.CommentResponse;
import com.grow.study_service.comment.presentation.dto.CommentSaveRequest;
import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * [게시물에 댓글을 저장하는 메서드]
     *
     * 이 메서드는 주어진 게시물 ID와 회원 ID를 기반으로 댓글을 생성하고 저장합니다.
     * 저장 전에 다음 검증을 수행합니다:
     * - 회원의 게시물 접근 권한 확인 (groupmemberRepository를 통해 단일 쿼리로 검증).
     * - 동일한 회원이 동일한 게시물에 동일한 내용의 댓글을 중복으로 등록하지 않도록 확인.
     *
     * 낙관적 락(Optimistic Locking)을 사용하여 동시성 문제를 해결합니다.
     * 트랜잭션 관리를 통해 데이터 일관성을 유지합니다.
     *
     * @param memberId 댓글을 등록하는 회원의 ID
     * @param postId 댓글이 등록될 게시물의 ID
     * @param request 댓글 저장 요청 객체 (부모 댓글 ID, 내용 포함)
     * @return 저장된 댓글 정보를 담은 CommentResponse 객체
     * @throws ServiceException 권한 없음(INVALID_POST_ACCESS) 또는 중복 댓글(COMMENT_ALREADY_EXISTS) 시 발생
     */
    @Override
    @Transactional
    public CommentResponse save(Long memberId, Long postId,
                                CommentSaveRequest request) {
        log.info("[COMMENT][SAVE][START] memberId={}, postId={} - 댓글 저장 요청 시작",
                memberId, postId);

        // 권한 검증 (단일 쿼리) -> 오류는 상세하게 적을 수 없는 단점
        if (!groupMemberRepository.existsByMemberIdAndPostGroup(postId, memberId)) {
            throw new ServiceException(ErrorCode.INVALID_POST_ACCESS);
        }

        // 한 사용자가 같은 Post 에 같은 댓글을 남길 수 없도록 중복 확인
        if (commentRepository.existsByPostIdAndMemberIdAndContent(
                postId, memberId, request.getContent()
        )) {
            throw new ServiceException(ErrorCode.COMMENT_ALREADY_EXISTS);
        }

        // Comment 생성 및 저장
        Comment comment = Comment.create(
                postId,
                memberId,
                request.getParentId(),
                request.getContent(),
                LocalDateTime.now()
        );

        Comment saved = commentRepository.save(comment);

        log.info("[COMMENT][SAVE][END] memberId={}, postId={}, commentId={} - 댓글 저장 성공",
                memberId, postId, saved.getCommentId());

        return CommentResponse.of(saved);
    }

    /**
     * 특정 게시물(postId)에 달린 모든 댓글을 트리 구조(List<CommentResponse>)로 반환한다.
     *
     * 1. commentRepository.getAllComments(postId) 로 게시물의 모든 댓글을 한 번에 조회한다.
     * 2. parentId 가 null 인 댓글(루트 댓글)만 필터링한다.
     * 3. 각 루트 댓글에 대해 mapToResponseWithReplies(…) 를 재귀 호출하여
     *    자식 댓글(replies)을 계층적으로 채운다.
     *
     * Transactional(readOnly = true) 로 조회 전용 트랜잭션에서 동작하며,
     * DB 상태를 변경하지 않는다.
     *
     * @param postId 댓글을 조회할 게시물 ID
     * @return 루트 댓글부터 대댓글까지 계층 구조가 완성된 CommentResponse 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId, Long memberId) {
        // 권한 검증
        if (!groupMemberRepository.existsByMemberIdAndPostGroup(postId, memberId)) {
            throw new ServiceException(ErrorCode.INVALID_POST_ACCESS);
        }

        // 1. 한 번의 쿼리로 게시물의 모든 댓글을 가져온다
        List<Comment> allComments = commentRepository.getAllComments(postId);

        // 2. parentId 가 null 인 루트 댓글만 필터링
        // 3. 각 루트 댓글을 CommentResponse 로 변환하면서 재귀적으로 replies 세팅
        return allComments.stream()
                .filter(comment -> comment.getParentId() == null)
                .map(comment -> mapToResponseWithReplies(comment, allComments))
                .toList();
    }

    /**
     * Comment 엔티티를 CommentResponse 로 변환한 뒤,
     * 재귀적으로 자식 댓글(replies)을 찾아 CommentResponse 에 채워 넣는 헬퍼 메서드.
     *
     * @param comment      변환할 대상 댓글
     * @param allComments  동일 게시물의 모든 댓글(루트·대댓글 포함)
     * @return replies 가 계층적으로 채워진 CommentResponse
     */
    private CommentResponse mapToResponseWithReplies(Comment comment, List<Comment> allComments) {
        // 현재 댓글을 부모로 갖는 자식 댓글 찾기 (parentId == comment.commentId)
        List<CommentResponse> replies = allComments.stream()
                .filter(c -> comment.getCommentId().equals(c.getParentId()))
                .map(c -> mapToResponseWithReplies(c, allComments))  // 재귀 호출
                .toList();

        // CommentResponse 생성 및 자식 댓글 세팅
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .parentId(comment.getParentId())
                .memberId(comment.getMemberId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .replies(replies)
                .build();
    }
}
