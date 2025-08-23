package com.grow.study_service.post.application.delete;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.post.application.file.delete.FileDeleteService;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDeleteServiceImpl implements PostDeleteService {

    private final PostRepository postRepository;
    private final FileDeleteService fileDeleteService;

    /**
     * [게시글 삭제 메서드]
     * <p>
     * 지정된 회원이 작성한 게시물을 삭제합니다. 게시물 존재 여부와 작성자 권한을 확인한 후, 데이터베이스에서 게시물을 삭제하고 연결된 파일도 함께 제거합니다.
     *
     * <ol>
     *     <li>게시물 ID를 통해 게시물을 조회하고, 존재하지 않으면 예외를 발생시킵니다.</li>
     *     <li>요청한 회원이 게시물의 작성자인지 검증합니다. (아니면 예외 발생)</li>
     *     <li>데이터베이스에서 게시물을 삭제합니다.</li>
     *     <li>게시물에 연결된 파일을 서버에서 삭제합니다.</li>
     * </ol>
     *
     * @param memberId 삭제를 요청한 회원의 ID
     * @param postId 삭제할 게시물의 ID
     *
     * @throws ServiceException 게시물이 존재하지 않을 경우
     * @throws com.grow.study_service.common.exception.domain.DomainException 회원이 게시물의 작성자가 아닐 경우
     *
     * @implNote 이 메서드는 @Transactional 어노테이션을 통해 트랜잭션으로 관리되며, 삭제 작업 중 예외 발생 시 롤백됩니다. 파일 삭제는 별도의 서비스(fileDeleteService)를 호출하여 처리합니다.
     *
     * @see PostRepository#delete(Post)
     * @see FileDeleteService#deleteFilesForPost(Long)
     */
    @Override
    @Transactional
    public void deletePost(Long memberId, Long postId) {
        log.info("[POST][DELETE][START] 게시물 삭제 시작 memberId={}, postId={}", memberId, postId);

        Post post = findPostOrThrow(postId);

        post.validateMember(memberId); // 본인이 작성한 글인지 확인하기

        postRepository.delete(post); // 게시글 삭제
        fileDeleteService.deleteFilesForPost(postId); // 게시글에 연결된 파일 삭제 (서버의 물리 파일도 삭제)
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException(ErrorCode.POST_NOT_FOUND));
    }
}