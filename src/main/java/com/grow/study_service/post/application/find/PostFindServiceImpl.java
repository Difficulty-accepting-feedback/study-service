package com.grow.study_service.post.application.find;

import com.grow.study_service.board.domain.repository.BoardRepository;
import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.groupmember.domain.repository.GroupMemberRepository;
import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.FileMetaRepository;
import com.grow.study_service.post.domain.repository.PostRepository;
import com.grow.study_service.post.presentation.dto.response.PostResponse;
import com.grow.study_service.post.presentation.dto.response.PostSimpleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostFindServiceImpl implements PostFindService {

    private final PostRepository postRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final FileMetaRepository fileRepository;
    private final BoardRepository boardRepository;

    /**
     * 게시물 단건 조회
     * <p>
     * 주어진 게시물 ID와 사용자 ID를 기반으로 게시물을 조회합니다.
     * <ol>
     *     <li>게시물이 존재하는지 확인하고 없으면 예외를 발생시킵니다.</li>
     *     <li>사용자가 해당 게시판(그룹)의 멤버인지 검증합니다.</li>
     *     <li>게시물에 연결된 첨부 파일 목록을 조회합니다.</li>
     *     <li>게시물 정보와 첨부 파일 정보를 포함한 응답 객체를 생성하여 반환합니다.</li>
     * </ol>
     *
     * @param postId   조회할 게시물의 ID
     * @param memberId 현재 로그인한 회원의 ID (게시판 접근 권한 검증에 사용됨)
     * @return {@link PostResponse} 게시물 정보와 첨부 파일 목록을 포함하는 응답 DTO
     * @throws ServiceException 게시물이 존재하지 않거나 사용자가 소속된 그룹의 멤버가 아닌 경우 발생
     * @see PostResponse
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse findById(Long postId, Long memberId) {
        log.info("[NOTICE][POST][FIND][START] 게시물 조회 시작 postId={}", postId);

        Post post = findPostOrThrow(postId);
        validateGroupMember(memberId, post.getBoardId());

        List<FileMeta> attachedFiles = fileRepository.findAllByPostId(postId);

        log.info("[NOTICE][POST][FIND][END] 게시물 조회 완료 postId={}, 첨부 파일={}개", postId, attachedFiles.size());

        return PostResponse.of(post, attachedFiles);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostSimpleResponse> getPostList(Long memberId, Long boardId) {
        log.info("[NOTICE][POST][FIND][LIST] 게시물 목록 조회 시작 memberId={}, boardId={}", memberId, boardId);

        // 게시판 존재 여부 검증
        boardRepository.findById(boardId)
                .orElseThrow(() -> new ServiceException(ErrorCode.BOARD_NOT_FOUND));

        // 게시판 멤버 여부 검증
        validateGroupMember(memberId, boardId);

        // 게시물 목록 조회 및 변환
        List<PostSimpleResponse> postList = postRepository.findByBoardId(boardId)
                .stream()
                .map(PostSimpleResponse::of)
                .toList();

        // 빈 리스트인 경우 조기 반환
        if (postList.isEmpty()) {
            log.info("[NOTICE][POST][FIND][LIST] 게시물 목록이 없습니다. 게시판={}", boardId);
            return List.of();
        }

        log.info("[NOTICE][POST][FIND][LIST] 게시물 목록 조회 완료. 게시물 수={}, 게시판={}", postList.size(), boardId);
        return postList;
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException(ErrorCode.POST_NOT_FOUND));
    }

    private void validateGroupMember(Long memberId, Long boardId) {
        if (!groupMemberRepository.existsByGroupIdAndMemberId(memberId, boardId)) {
            throw new ServiceException(ErrorCode.MEMBER_NOT_IN_GROUP);
        }
    }
}
