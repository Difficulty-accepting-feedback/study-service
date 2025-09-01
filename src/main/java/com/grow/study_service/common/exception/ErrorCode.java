package com.grow.study_service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 📌 1. 그룹(Group) 관련
     */
    GROUP_NAME_IS_EMPTY("400", "그룹 이름은 비어있을 수 없습니다."),
    GROUP_DESCRIPTION_IS_EMPTY("400", "그룹 설명은 비어있을 수 없습니다."),
    GROUP_ID_IS_EMPTY("400", "그룹 아이디는 비어있을 수 없습니다."),
    CATEGORY_IS_EMPTY("400", "카테고리는 비어있을 수 없습니다."),
    GROUP_LEADER_REQUIRED("403", "이 기능은 그룹장 권한이 있는 사용자만 사용할 수 있습니다. 그룹장 권한 요청 후 다시 시도해 주세요."),
    GROUP_NOT_FOUND("404", "그룹을 찾을 수 없습니다." ),
    GROUP_ALREADY_JOINED("409", "이미 가입된 그룹입니다."),
    JOIN_REQUEST_ALREADY_SENT("409", "이미 가입 요청을 보냈습니다." ),

    /**
     * 📌 2. 그룹 멤버(Group Member) 관련
     */
    GROUP_MEMBER_MEMBER_ID_IS_EMPTY("400", "그룹에 속해있는 멤버의 아이디는 비어있을 수 없습니다."),
    GROUP_MEMBER_GROUP_ID_IS_EMPTY("400", "그룹 멤버가 속해있는 그룹의 아이디는 비어있을 수 없습니다."),
    GROUP_MEMBER_ROLE_IS_EMPTY("400", "그룹 멤버의 역할은 비어있을 수 없습니다."),
    GROUP_MEMBER_JOINED_AT_IS_EMPTY("400", "그룹 멤버가 가입한 시각은 비어있을 수 없습니다."),
    GROUP_MEMBER_ID_IS_EMPTY("400", "그룹 멤버 아이디는 비어있을 수 없습니다."),
    GROUP_MEMBER_NOT_FOUND("404", "그룹 멤버를 찾을 수 없습니다."),
    MEMBER_NOT_IN_GROUP("403", "그룹 멤버가 아닙니다. 접근 권한이 없습니다."),
    GROUP_OR_LEADER_NOT_FOUND("404", "그룹 또는 그룹의 리더를 찾을 수 없습니다. " ),


    /**
     * 📌 3. 게시판(Board) 관련
     */
    BOARD_NAME_IS_EMPTY("400", "게시판 이름은 비어있을 수 없습니다."),
    BOARD_DESCRIPTION_IS_EMPTY("400", "게시판 설명은 비어있을 수 없습니다."),
    BOARD_TYPE_IS_EMPTY("400", "게시판 타입은 비어있을 수 없습니다."),
    BOARD_ID_IS_EMPTY("400", "게시판 아이디는 비어있을 수 없습니다."),
    BOARD_CREATED_AT_IS_EMPTY("400", "게시판 생성 시각은 비어있을 수 없습니다."),
    BOARD_NOT_FOUND("404", "게시판을 찾을 수 없습니다."),

    /**
     * 📌 4. 공지사항(Notice) 관련
     */
    NOTICE_CONTENT_IS_EMPTY("400", "공지 내용은 비어있을 수 없습니다."),
    NOTICE_ID_IS_EMPTY("400", "공지사항 아이디는 비어있을 수 없습니다."),
    NOTICE_NOT_FOUND("404", "공지사항을 찾을 수 없습니다."),
    GROUP_NOT_MATCH("403", "그룹과 게시판이 일치하지 않습니다."),

    /**
     * 📌 4. 게시글(Post) 관련
     */
    TITLE_IS_EMPTY("400", "제목은 비어있을 수 없습니다."),
    CONTENT_IS_EMPTY("400", "내용은 비어있을 수 없습니다."),
    POST_ID_IS_EMPTY("400", "게시글 아이디는 비어있을 수 없습니다."),
    ORIGINAL_NAME_IS_EMPTY("400", "원본 파일 이름은 비어있을 수 없습니다."),
    STORED_NAME_IS_EMPTY("400", "저장된 파일 이름은 비어있을 수 없습니다."),
    CONTENT_TYPE_IS_EMPTY("400", "파일 타입은 비어있을 수 없습니다."),
    SIZE_IS_NEGATIVE("400", "파일 크기는 0보다 커야합니다."),
    PATH_IS_EMPTY("400", "파일 경로는 비어있을 수 없습니다."),
    FILE_UPLOAD_FAILED("500", "파일 업로드에 실패했습니다."),
    POST_NOT_FOUND("404", "게시글을 찾을 수 없습니다."),
    FILE_NOT_FOUND("404", "파일을 찾을 수 없습니다."),
    FILE_PATH_INVALID("400", "파일 경로가 올바르지 않습니다."),
    NOT_AUTHORIZED_USER("403", "글을 수정할 권한이 없습니다."),
    FILE_DELETE_FAILED("500", "파일 삭제에 실패했습니다."),

    /**
     * 📌 5. 댓글(comment) 관련
     */
    COMMENT_CONTENT_IS_EMPTY("400", "댓글 내용은 비어있을 수 없습니다."),
    INVALID_POST_ACCESS("403", "이 게시글에 접근할 권한이 없습니다. postId나 그룹 가입 상태를 확인해 주세요. "),
    COMMENT_ALREADY_EXISTS("409", "이미 동일한 댓글이 존재합니다." ),
    COMMENT_NOT_FOUND("404", "댓글을 찾을 수 없습니다."),
    INVALID_COMMENT_ACCESS("403", "이 댓글에 접근할 권한이 없습니다." ),
    ;

    private final String code;
    private final String message;
}
