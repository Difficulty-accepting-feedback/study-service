package com.grow.study_service.post.domain.model;

import com.grow.study_service.common.exception.ErrorCode;
import com.grow.study_service.common.exception.domain.DomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FileMeta {

    private final Long fileId;       // 파일 아이디 (영속 후 할당)
    private final Long postId;       // 게시글 아이디
    private final String originalName; // 업로드 당시 파일명
    private final String storedName;   // 서버에 저장된 파일명
    private final String contentType;  // MIME type
    private final long size;           // 바이트 크기
    private final String path;         // 실제 저장 경로 (디렉토리 포함)
    private final LocalDateTime uploadedAt;

    /**
     * 생성 메서드
     */
    public static FileMeta create(Long postId,
                                  String originalName,
                                  String storedName,
                                  String contentType,
                                  long size,
                                  String path) {

        if (postId == null) throw new DomainException(ErrorCode.POST_ID_IS_EMPTY);
        if (isBlank(originalName)) throw new DomainException(ErrorCode.ORIGINAL_NAME_IS_EMPTY);
        if (isBlank(storedName)) throw new DomainException(ErrorCode.STORED_NAME_IS_EMPTY);
        if (isBlank(contentType)) throw new DomainException(ErrorCode.CONTENT_TYPE_IS_EMPTY);
        if (size < 0) throw new DomainException(ErrorCode.SIZE_IS_NEGATIVE);
        if (isBlank(path)) throw new DomainException(ErrorCode.PATH_IS_EMPTY);

        return new FileMeta(
                null,
                postId,
                originalName,
                storedName,
                contentType,
                size,
                path,
                LocalDateTime.now()
        );
    }

    /**
     * 조회용 메서드
     */
    public static FileMeta of(Long fileId,
                              Long postId,
                              String originalName,
                              String storedName,
                              String contentType,
                              long size,
                              String path,
                              LocalDateTime uploadedAt) {
        return new FileMeta(
                fileId, postId, originalName, storedName, contentType, size, path, uploadedAt
        );
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}