package com.grow.study_service.post.application.file.delete;

import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.repository.FileMetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDeleteServiceImpl implements FileDeleteService {

    private final FileMetaRepository fileMetaRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_KEY = "failed_file";


    /**
     * [파일 삭제 메서드]
     * <p>
     * 지정된 게시물 ID에 연결된 파일들을 삭제합니다. 데이터베이스에서 파일 메타정보를 제거하고, 서버의 물리 파일도 함께 삭제합니다.
     * 삭제 과정에서 예외가 발생하면 Redis에 실패 정보를 저장하여 후속 처리를 유도합니다.
     * 내부 처리 절차는 다음과 같습니다.
     *
     * <ol>
     *     <li>게시물 ID를 통해 연결된 파일 메타정보를 조회합니다. (없으면 삭제를 스킵합니다.)</li>
     *     <li>각 파일 메타정보를 데이터베이스에서 삭제합니다.</li>
     *     <li>서버의 물리 파일을 삭제합니다. (실패 시 Redis에 저장하여 스케줄링 처리)</li>
     * </ol>
     *
     * @param postId 삭제할 파일들이 연결된 게시물의 ID
     *
     * @throws Exception 파일 삭제 중 내부 예외 발생 시 (로그로 기록되며, 메서드 throws는 없음)
     *
     * @see FileMetaRepository#findByPostId(Long)
     * @see FileMetaRepository#delete(FileMeta)
     * @see #cleanupStoredFiles(List)
     */
    @Override
    @Transactional
    public void deleteFilesForPost(Long postId) {
        log.info("[FILE][DELETE][START] postId={} - 파일 삭제 시작", postId);

        List<FileMeta> metas = fileMetaRepository.findByPostId(postId);

        if (metas.isEmpty()) {
            log.info("[FILE][DELETE][SKIP] postId={} - 파일 없음, 삭제 진행 취소", postId);
            return;
        }

        List<Path> paths = new ArrayList<>();
        for (FileMeta meta : metas) {
            paths.add(Paths.get(meta.getPath()));
            fileMetaRepository.delete(meta); // 파일 메타정보 삭제

            try {
                cleanupStoredFiles(paths); // 물리 파일 삭제
            } catch (Exception e) {
                log.warn("[FILE][DELETE][ERROR] postId={} - 파일 삭제 중 예외 발생, 삭제 진행 취소: {}",
                        postId, e.toString());

                // 실패 시 redis 에 저장 -> 스케줄링 및 이벤트 발행
                String entry = meta.getStoredName() + ":" + meta.getPath(); // failed_file - 저장명:저장경로
                redisTemplate.opsForSet().add(REDIS_KEY, entry);
            }
        }

        log.info("[FILE][DELETE][END] postId={} - 파일 삭제 완료", postId);
    }

    /**
     * 저장된 물리 파일 정리
     * <p>
     * 업로드 과정에서 예외가 발생한 경우, 이미 저장된 파일들을 삭제하여 일관성을 유지합니다.
     *
     * @param paths 삭제 대상 파일 경로 리스트
     * @implNote 삭제 중 발생하는 예외는 무시합니다. 업로드 전반의 실패를 상위 예외로 처리합니다.
     */
    private void cleanupStoredFiles(List<Path> paths) {
        int deleted = 0;
        for (Path p : paths) {
            try {
                boolean ok = Files.deleteIfExists(p);
                deleted += ok ? 1 : 0;
                log.debug("[NOTICE][FILE][CLEANUP] path={}, deleted={}", p, ok);
            } catch (Exception ignored) {
                log.warn("[NOTICE][FILE][CLEANUP][IGNORE] path={} - 삭제 실패: {}", p, ignored.toString());
            }
        }
    }
}
