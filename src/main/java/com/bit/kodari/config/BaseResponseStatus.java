package com.bit.kodari.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),


    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),



    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패하였습니다."),
    MODIFY_FAIL_POST(false, 4070, "게시글 수정에 실패하였습니다."),
    MODIFY_FAIL_POST_COMMENT(false, 4071, "게시글 댓글 수정에 실패하였습니다."),
    MODIFY_FAIL_POST_LIKE(false, 4072, "좋아요 수정에 실패하였습니다."),
    MODIFY_FAIL_POST_REPLY(false, 4073, "답글 수정에 실패하였습니다."),

    //삭제
    DELETE_FAIL_POST(false, 4074, "게시글 삭제에 실패하였습니다."),
    DELETE_FAIL_POST_COMMENT(false, 4075, "게시글 댓글 삭제에 실패하였습니다."),

    //USER 확인
    USER_NOT_EQUAL(false, 4076, "게시글 유저가 아닙니다."),
    USER_NOT_EQUAL_COMMENT(false, 4077, "댓글 유저가 아닙니다."),
    USER_NOT_EQUAL_LIKE(false, 4078, "좋아요 누른 유저가 아닙니다."),

    //내용확인
    EMPTY_CONTENT(false, 4079, "내용이 없습니다."),

    //글 존재여부
    IMPOSSIBLE_POST(false, 4080, "게시글이 존재하지 않습니다."),
    IMPOSSIBLE_POST_COMMENT(false, 4081, "댓글이 존재하지 않습니다."),
    IMPOSSIBLE_POST_LIKE_DELETE(false, 4082, "게시글이 존재하기때문에 삭제가 불가능합니다."),

    //글자수 확인
    OVER_CONTENT(false, 4083, "글자수가 초과되었습니다."),

    //같은 LIKE 타입을 고른 경우
    EQUAL_LIKE_TYPE(false, 4084, "이전과 같습니다."),

    //삭제 불가
    DELETE_FAIL_POST_LIKE(false, 4085, "삭제가 되지 않았습니다."),
    DELETE_FAIL_COMMENT_REPLY(false, 4086, "답글 삭제가 되지 않았습니다."),


    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");



    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
