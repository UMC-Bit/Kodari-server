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
    POST_USERS_EMPTY_NICKNAME(false, 2018, "닉네임을 입력해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2019, "비밀번호를 입력해주세요."),
    POST_USERS_EXISTS_NICKNAME(false,2020,"중복된 닉네임입니다."),
    POST_USERS_LENGTH_NICKNAME(false,2029,"닉네임 길이는 최소 1자 최대 15자입니다."),
    POST_USERS_INVALID_PASSWORD(false,2030,"비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상이고 특수문자는 !@#$%^*+=-만 사용 가능합니다."),
    POST_USERS_INVALID_NICKNAME(false,2031,"닉네임은 영어,한글,숫자만 입력해주세요."),

    // [Patch] /trades
    PRICE_RANGE_ERROR(false,2021,"가격의 크기를 확인해주세요."),
    AMOUNT_RANGE_ERROR(false,2022,"코인 갯수를 확인해주세요."),
    FEE_RANGE_ERROR(false,2023,"수수료를 확인해주세요."),
    EMPTY_CATEGORY(false, 2024, "매수/매도를 입력해주세요."),
    PORTIDX_RANGE_ERROR(false,2025,"포트폴리오 인덱스를 확인해주세요."),
    COINIDX_RANGE_ERROR(false,2026,"코인 인덱스를 확인해주세요."),
    EMPTY_DATE(false,2027,"거래시각을 입력해주세요."),
    ACCONTIDX_RANGE_ERROR(false,2028,"계좌 인덱스를 확인해주세요."),
    LACK_OF_PROPERTY(false,2032,"계좌의 현금 잔액 부족"),
    LACK_OF_AMOUNT(false,2033,"계좌의 코인 갯수 부족"),
    POST_ACCOUNT_NAME_NULL(false, 2040, "계좌이름을 입력해주세요."),
    INACTIVE_PORTFOLIO(false, 2041, "삭제된 포트폴리오입니다."),
=========
>>>>>>>>> Temporary merge branch 2



    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),


    // [GET] /users
    GET_USERS_NOT_EXISTS(false,3015,"등록된 유저가 없습니다."),
    GET_USERS_NOT_EXISTS_NICKNAME(false,3016,"없는 닉네임 입니다."),
    GET_USERS_NOT_EXISTS_EMAIL(false,3017,"없는 이메일 입니다."),
    GET_USERS_NOT_EXISTS_USERIDX(false,3021,"없는 유저인덱스 입니다."),

    GET_TRADES_NOT_EXISTS(false,3018,"없는 포트폴리오 및 코인 입니다."),

    GET_SYMBOLS_NOT_EXISTS(false,3019,"없는 계좌 및 코인코드입니다."),
    GET_PROFITS_NOT_EXISTS(false,3020,"수익내역이 없습니다."),

    ALREADY_DELETED_PROFIT(false,3021,"이미 삭제된 수익내역입니다."),
    ALREADY_DELETED_USER(false,3022,"이미 삭제된 회원입니다."),

    ALREADY_DELETED_TRADE(false,3023,"이미 삭제된 거래내역입니다."),


    FAILED_TO_PROPERTY_RES(false,3040,"없는 계좌입니다."),
    OVER_PORT_THREE(false, 3041, "등록할 수 있는 포트폴리오 갯수를 초과하였습니다."),
    OVER_ACCOUNT_THREE(false, 3042, "등록할 수 있는 계좌 갯수를 초과하였습니다."),




    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    GET_UPBITAPI_ERROR(false, 4013, "업비트 API 응답 에러입니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),
    //40-69
    MODIFY_FAIL_ACCOUNTNAME(false, 4040, "계좌 이름 수정 실패"),
    MODIFY_FAIL_PROPERTY(false, 4041, "현금 자산 수정 실패"),
    MODIFY_FAIL_ACCOUNT_STATUS(false, 4042, "계좌 활성 상태 수정 실패"),
    DUPLICATED_ACCOUNT_NAME(false, 4043, "계좌 이름 중복으로 수정 실패"),
    PROPERTY_RANGE_ERROR(false, 4044, "현금 자산 범위 오류로 수정 실패"),
    MODIFY_FAIL_USERCOIN_STATUS(false, 4045, "소유 코인 삭제 실패"),
    MODIFY_FAIL_ALL_USERCOIN_STATUS(false, 4046, "소유 코인 삭제 실패"),
    MODIFY_FAIL_USERCOIN(false, 4047, "소유 코인 수정 실패"),
    MODIFY_FAIL_PRICE_AVG(false, 4048, "매수평단가 수정 실패"),
    MODIFY_FAIL_PORTFOLIO(false, 4049, "포트폴리오 삭제 실패"),
    DUPLICATED_PORTFOLIO(false, 4050, "포트폴리오 중복으로 생성 실패"),
    MODIFY_FAIL_TOTAL(false, 4051, "총자산 수정 실패"),
    PRICE_AVG_RANGE_ERROR(false, 4052, "매수평단가 범위 오류로 수정 실패"),
    //AMOUNT_RANGE_ERROR(false, 4053, "코인 amount 범위 오류로 수정 실패"),
    COIN_AMOUNT_OVER(false, 4054, "매도하는 코인이 기존보다 많습니다."),
    DELETE_FAIL_REPRESENT(false, 4055, "대표 코인 삭제에 실패하였습니다."),
    COIN_AMOUNT_ZERO(false, 4056, "코인이 전부 매도되었습니다."),

    //수정 실패
    MODIFY_FAIL_POST(false, 4070, "게시글 수정에 실패하였습니다."),
    MODIFY_FAIL_POST_COMMENT(false, 4071, "게시글 댓글 수정에 실패하였습니다."),
    MODIFY_FAIL_POST_LIKE(false, 4072, "좋아요/싫어요 수정에 실패하였습니다."),
    MODIFY_FAIL_POST_REPLY(false, 4073, "답글 수정에 실패하였습니다."),

    //삭제 실패
    DELETE_FAIL_POST(false, 4074, "게시글 삭제에 실패하였습니다."),
    DELETE_FAIL_POST_COMMENT(false, 4075, "댓글 삭제에 실패하였습니다."),
    DELETE_FAIL_POST_LIKE(false, 4076, "좋아요/싫어요 삭제에 실패하였습니다."),
    DELETE_FAIL_COMMENT_REPLY(false, 4077, "답글 삭제에 실패하였습니다."),
    DELETE_FAIL_COMMENT_LIKE(false, 4078, "댓글의 좋아요 삭제에 실패하였습니다."),

    //USER 확인
    USER_NOT_EQUAL(false, 4079, "게시글 유저가 아닙니다."),
    USER_NOT_EQUAL_COMMENT(false, 4080, "댓글 유저가 아닙니다."),
    USER_NOT_EQUAL_LIKE(false, 4081, "좋아요/싫어요 누른 유저가 아닙니다."),
    USER_NOT_EQUAL_REPLY(false, 4082, "답근 유저가 아닙니다."),

    //글 존재여부
    IMPOSSIBLE_POST(false, 4083, "게시글이 존재하지 않습니다."),
    IMPOSSIBLE_POST_COMMENT(false, 4084, "댓글이 존재하지 않습니다."),

    //내용확인
    EMPTY_CONTENT(false, 4085, "내용이 없습니다."),

    //글자수 확인
    OVER_CONTENT(false, 4086, "글자수가 초과되었습니다.");





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
