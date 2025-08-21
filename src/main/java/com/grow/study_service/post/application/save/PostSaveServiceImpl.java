package com.grow.study_service.post.application.save;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.post.application.file.FileService;
import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.model.Post;
import com.grow.study_service.post.domain.repository.PostRepository;
import com.grow.study_service.post.presentation.dto.request.PostSaveRequest;
import com.grow.study_service.post.presentation.dto.request.PostUpdateRequest;
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
        log.info("[POST][SAVE][START] 게시물 저장 시작 - memberId={}, boardId={}, fileCount={}",
                memberId, request.getBoardId(), (files == null ? 0 : files.size()));

        Post post = Post.create(
                request.getBoardId(),
                memberId,
                request.getTitle(),
                request.getContent()
        );

        Post saved = postRepository.save(post);
        log.info("[POST][SAVED] postId={}, boardId={}, memberId={}",
                saved.getPostId(), saved.getBoardId(), saved.getMemberId());

        List<FileMeta> savedMetas = (files == null || files.isEmpty())
                ? Collections.emptyList() // 파일이 없을 경우 empty list 반환
                : fileService.storeFilesForPost(saved.getPostId(), files);

        log.info("[POST][SAVE][END] 게시물 저장 완료 - postId={}, savedCount={}, fileCount={}",
                saved.getPostId(), savedMetas.size(), (files == null ? 0 : files.size()));
        return PostResponse.of(saved, savedMetas);
    }

    /**
     * 게시글 수정 메서드
     * <p>
     * 게시글 ID와 수정 요청 정보를 받아 해당 게시글을 업데이트하고, 수정된 게시글 정보를 반환합니다.
     * 내부적으로 게시글 존재 여부 확인, 권한 검증, 내용 변경 처리 등을 수행합니다.
     *
     * <ol>
     *     <li>게시글 존재 여부 확인</li>
     *     <li>사용자 권한 검증</li>
     *     <li>게시글 내용 업데이트</li>
     *     <li>변경 내용 저장 및 반환</li>
     * </ol>
     *
     * @param memberId 수정 요청자의 ID (권한 검증용)
     * @param postId 수정할 게시글의 ID
     * @param request 게시글 수정 요청 DTO (제목, 내용 등 포함)
     * @param files 첨부 파일 목록 (null 또는 빈 리스트일 수 있음)
     *
     * @throws ServiceException 게시글이 없거나 권한이 없을 경우 발생
     */
    @Override
    @Transactional
    public void updatePost(Long memberId, Long postId,
                           PostUpdateRequest request, List<MultipartFile> files) {

        log.info("[POST][UPDATE][START] 게시물 수정 시작 - postId={}, memberId={}", postId, memberId);

        Post post = postRepository.findById(postId).orElseThrow(()
                -> new ServiceException(ErrorCode.POST_NOT_FOUND));

        post.validateMember(memberId); // 본인이 작성한 글인지 확인하기

        post.update(request.getTitle(), request.getContent()); // 글 내용 업데이트 + 수정 시간 제공 + null 체크

        Post saved = postRepository.save(post);// 업데이트된 게시글 저장

        if (files != null && !files.isEmpty()) { // 첨부 파일이 있을 경우
            fileService.deleteFilesForPost(postId); // 기존에 저장된 파일 삭제
            fileService.storeFilesForPost(saved.getPostId(), files); // 파일이 같음을 확인할 수 없음... 그냥 업데이트
        }

        log.info("[POST][UPDATE][END] 게시물 수정 완료 - postId={}, memberId={}", postId, memberId);
    }
}
