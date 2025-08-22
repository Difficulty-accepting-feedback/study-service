package com.grow.study_service.post.application.file;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.service.ServiceException;
import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.repository.FileMetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMetaRepository fileMetaRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 게시글 첨부 파일 저장
     * <p>
     * 전달된 파일 목록을 서버 파일시스템에 저장한 뒤, 각 파일의 메타정보(FileMeta)를 생성하여 DB에 저장합니다.
     * 저장이 완료된 파일들의 메타정보 리스트를 반환합니다.
     *
     * <ol>
     *     <li>파일 목록이 null 또는 비어 있으면 빈 리스트 반환</li>
     *     <li>파일별로 유효성 검사(비어있는 파일 skip) 수행</li>
     *     <li>파일명 정규화(sanitize) 및 저장 경로 생성(예: {uploadPath}/yyyy-MM-dd/UUID.ext)</li>
     *     <li>디렉터리 생성 후 MultipartFile.transferTo로 물리 파일 저장</li>
     *     <li>저장된 경로/이름/타입/크기로 FileMeta 생성 및 저장</li>
     *     <li>모든 파일 처리 완료 후 저장된 FileMeta 리스트 반환</li>
     * </ol>
     *
     * @param postId 게시글 ID (파일 메타정보의 소유 게시글 식별자)
     * @param files  업로드할 MultipartFile 목록 (null 또는 빈 리스트 가능)
     * @return 저장된 파일들의 FileMeta 리스트 (파일이 없거나 모두 skip된 경우 빈 리스트)
     * @throws com.grow.study_service.common.exception.service.ServiceException 파일 저장 중 I/O 예외 발생 시 저장된 파일들을 정리한 뒤 업로드 실패 에러로 래핑하여 던짐
     * @implNote 물리 파일 시스템과 DB를 함께 다루므로, 파일 저장 중 예외가 발생하면 이미 저장된 파일을 정리(cleanup)합니다.
     * 저장 경로는 업로드 루트 경로 하위에 날짜 폴더(yyyy-MM-dd)로 구성되며, 파일명은 UUID 기반으로 생성됩니다.
     * @see #resolveTargetPath(String)
     * @see #cleanupStoredFiles(java.util.List)
     */
    @Override
    @Transactional
    public List<FileMeta> storeFilesForPost(Long postId, List<MultipartFile> files) {
        log.info("[NOTICE][FILE][SAVE][START] postId={}, fileCount={} - 첨부 파일 저장 시작",
                postId, (files == null ? 0 : files.size()));

        if (files == null && files.isEmpty()) {
            log.info("[NOTICE][FILE][SAVE][SKIP] postId={} - 첨부 파일 없음, 빈 리스트 반환", postId);
            return Collections.emptyList(); // 파일이 없을 경우 empty list 반환
        }

        List<Path> storedPaths = new ArrayList<>();
        List<FileMeta> savedMetas = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    log.debug("[NOTICE][FILE][SKIP] postId={} - 비어있는 파일, 건너뜀", postId);
                    continue; // 파일이 없을 경우 continue
                }

                String originalName = file.getOriginalFilename();
                String contentType = file.getContentType();
                long size = file.getSize();

                Path targetPath = resolveTargetPath(originalName); // 저장 대상 경로 생성 (날짜 폴더 + UUID 기반 파일명)
                Files.createDirectories(targetPath.getParent()); // 디렉토리 생성 (이미 존재하는 경우는 무시)
                file.transferTo(targetPath.toFile()); // 물리 파일 저장
                storedPaths.add(targetPath); // 저장된 물리 파일 경로 저장

                String storedName = targetPath.getFileName().toString();
                String fullPath = targetPath.toAbsolutePath().toString();

                log.info("[NOTICE][FILE][SAVED] 파일 저장 확인 - postId={}, storedName={}, fullPath={}",
                        postId, storedName, fullPath);

                FileMeta meta = FileMeta.create(
                        postId,
                        originalName,
                        storedName,
                        contentType,
                        size,
                        fullPath
                );

                FileMeta saved = fileMetaRepository.save(meta);
                savedMetas.add(saved);
            }
            log.info("[NOTICE][FILE][SAVE][END] postId={}, savedCount={} - 첨부 파일 저장 완료",
                    postId, savedMetas.size());
            return savedMetas;
        } catch (IOException e) {
            log.warn("[NOTICE][FILE][ERROR] postId={} - 파일 저장 중 IOException 발생, 정리 수행 시작: {}",
                    postId, e.toString());
            cleanupStoredFiles(storedPaths);
            throw new ServiceException(e, ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * [게시글 연관 파일 삭제]
     * <p>
     * 주어진 게시글 ID에 연관된 모든 파일 메타정보를 조회하고, 해당 파일들을 파일시스템과 DB에서 삭제합니다.
     *
     * <ol>
     *     <li>게시글 ID로 파일 메타 목록 조회</li>
     *     <li>각 파일의 물리 파일 삭제</li>
     *     <li>파일 메타 DB 삭제</li>
     * </ol>
     *
     * @param postId 삭제 대상 게시글 ID
     *
     * @throws ServiceException 파일 삭제 중 오류 발생 시
     *
     * @implNote 트랜잭션 적용으로 데이터 일관성 유지, 삭제 실패 시 롤백
     */
    @Transactional
    public void deleteFilesForPost(Long postId) {
        List<FileMeta> metas = fileMetaRepository.findByPostId(postId);

        if (metas.isEmpty()) {
            log.info("[NOTICE][FILE][DELETE][SKIP] postId={} - 파일 없음, 삭제 진행 취소", postId);
            return;
        }

        List<Path> paths = new ArrayList<>();
        for (FileMeta meta : metas) {
            paths.add(Paths.get(meta.getPath()));
            fileMetaRepository.delete(meta); // 파일 메타정보 삭제
        }

        try {
            cleanupStoredFiles(paths); // 물리 파일 삭제
        } catch (Exception e) {
            log.warn("[NOTICE][FILE][DELETE][ERROR] postId={} - 파일 삭제 중 예외 발생, 삭제 진행 취소: {}",
                    postId, e.toString());
            throw new ServiceException(e, ErrorCode.FILE_DELETE_FAILED);
        }

        log.info("[NOTICE][FILE][DELETE][END] postId={} - 파일 삭제 완료", postId);
    }

    /**
     * [파일 다운로드 링크 생성]
     * <p>
     * 주어진 파일 ID를 기반으로 파일의 다운로드 링크를 생성합니다.
     * 파일 메타 정보를 조회하고, 유효한 파일 경로를 확인한 후 URL 리소스를 반환합니다.
     *
     * <ol>
     *     <li>파일 ID로 파일 메타 정보를 데이터베이스에서 조회합니다.</li>
     *     <li>파일 경로를 Path 객체로 변환합니다.</li>
     *     <li>URL 리소스를 생성하여 반환합니다.</li>
     * </ol>
     *
     * @param fileId 파일의 고유 ID (Long 타입)
     *
     * @return 파일 다운로드를 위한 Resource 객체 (UrlResource)
     *
     * @throws ServiceException 파일이 존재하지 않을 경우 (ErrorCode.FILE_NOT_FOUND) 또는 파일 경로가 유효하지 않을 경우 (ErrorCode.FILE_PATH_INVALID)
     *
     * @implNote 파일 경로가 잘못된 경우 MalformedURLException을 ServiceException으로 래핑하여 처리합니다.
     *           이 메서드는 파일 시스템 접근을 포함하므로, 보안 및 권한 확인이 필요할 수 있습니다.
     *
     * @see FileMetaRepository#findById(Long)
     */
    @Override
    public Resource getDownloadLink(Long fileId) {
        FileMeta fileMeta = fileMetaRepository.findById(fileId).orElseThrow(() ->
                new ServiceException(ErrorCode.FILE_NOT_FOUND));

        Path filePath = Paths.get(fileMeta.getPath());
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new ServiceException(e, ErrorCode.FILE_PATH_INVALID);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileMeta getFileMeta(Long fileId) {
        return fileMetaRepository.findById(fileId).orElseThrow(() ->
                new ServiceException(ErrorCode.FILE_NOT_FOUND));
    }

    /**
     * 파일 저장 대상 경로 생성
     * <p>
     * 업로드 루트 경로 하위에 날짜별 디렉터리(yyyy-MM-dd)를 생성하고, UUID 기반 파일명을 확장자와 함께 구성합니다.
     * 예: {uploadPath}/2025-08-18/f1e2d3c4b5a697887766554433221100.pdf
     *
     * @param originalName 원본 파일명
     * @return 물리 저장 대상 Path
     * @implNote 확장자는 원본 파일명에서 마지막 '.' 기준으로 추출하며, 없으면 확장자 없이 저장됩니다.
     */
    private Path resolveTargetPath(String originalName) {
        String date = LocalDate.now().toString(); // yyyy-MM-dd
        String ext = extractExtension(originalName); // 확장자 ("." 제외)
        String base = UUID.randomUUID().toString().replace("-", "");
        String stored = ext.isEmpty() ? base : base + "." + ext;

        Path path = Paths.get(uploadPath, date, stored);
        log.debug("[NOTICE][FILE][PATH] originalName={}, resolvedPath={}", originalName, path);
        return path;
    }

    /**
     * 파일명에서 확장자 추출
     * <p>
     * 파일명의 마지막 '.' 이후 문자열을 확장자로 간주합니다. 확장자가 없으면 빈 문자열을 반환합니다.
     *
     * @param filename 파일명
     * @return 확장자(점 제외), 확장자가 없으면 빈 문자열
     */
    private String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.'); // pdf, jpg, png 등의 확장자를 찾음
        if (idx < 0) return "";
        return filename.substring(idx + 1);
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
