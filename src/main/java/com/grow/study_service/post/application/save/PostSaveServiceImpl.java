package com.grow.study_service.post.application.save;

import com.grow.study_service.post.application.file.FileService;
import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.PostRepository;
import com.grow.study_service.post.presentation.dto.request.PostSaveRequest;
import com.grow.study_service.post.presentation.dto.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSaveServiceImpl implements PostSaveService {

    private final PostRepository postRepository;
    private final FileService fileService;

    /**
     * 게시글 생성
     * <p>
     * 게시글을 생성하고, 첨부 파일이 있을 경우 파일 저장을 수행하여 파일 메타정보와 함께 응답을 반환합니다.
     *
     * <ol>
     *     <li>요청 데이터를 이용해 Post 엔티티 생성</li>
     *     <li>PostRepository를 통해 게시글 저장</li>
     *     <li>첨부 파일이 존재하면 FileService를 통해 파일 저장 및 FileMeta 저장</li>
     *     <li>저장된 게시글 및 파일 메타정보를 PostResponse로 변환하여 반환</li>
     * </ol>
     *
     * @param memberId 게시글 작성 회원 ID
     * @param request  게시글 저장 요청 DTO (boardId, title, content 포함)
     * @param files    첨부 파일 목록 (null 또는 빈 리스트일 수 있음)
     *
     * @return 생성된 게시글 정보와 파일 메타정보 리스트를 포함한 응답 DTO
     *
     * @throws com.grow.study_service.common.exception.service.ServiceException
     *         파일 저장 과정에서 I/O 오류 등으로 업로드가 실패한 경우
     *
     * @implNote 트랜잭션이 적용되며, 파일 저장은 영속화와 분리된 외부 자원 접근을 포함합니다.
     *           파일 저장 실패 시 ServiceException을 던지며, 저장된 물리 파일은 정리됩니다.
     *
     * @see com.grow.study_service.post.application.file.FileService#storeFilesForPost(Long, java.util.List)
     * @see com.grow.study_service.post.presentation.dto.response.PostResponse
     */
    @Override
    @Transactional
    public PostResponse createPost(Long memberId, PostSaveRequest request, List<MultipartFile> files) {
        log.info("[NOTICE][POST][SAVE][START] 게시물 저장 시작 - memberId={}, boardId={}, fileCount={}",
                memberId, request.getBoardId(), files.size());

        Post post = Post.create(
                request.getBoardId(),
                memberId,
                request.getTitle(),
                request.getContent()
        );

        Post saved = postRepository.save(post);
        log.info("[NOTICE][POST][SAVED] postId={}, boardId={}, memberId={}",
                saved.getPostId(), saved.getBoardId(), saved.getMemberId());

        List<FileMeta> savedMetas = (files == null || files.isEmpty())
                ? Collections.emptyList() // 파일이 없을 경우 empty list 반환
                : fileService.storeFilesForPost(saved.getPostId(), files);

        log.info("[NOTICE][POST][SAVE][END] 게시물 저장 완료 - postId={}, savedCount={}, fileCount={}",
                saved.getPostId(), savedMetas.size(), files.size());
        return PostResponse.of(saved, savedMetas);
    }
}
