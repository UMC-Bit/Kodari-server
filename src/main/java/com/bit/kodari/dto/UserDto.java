package com.bit.kodari.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {
    // 유저 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private int userIdx;
        private String nickName;
        private String email;
        private String password;
        private String profileImgUrl;
        private String authKey = " "; //이메일 인증키
        private String status;
    }

    // 회원가입 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class PostUserReq{
        private String nickName;
        private String email;
        private String password;
        private String profileImgUrl;
        private String authKey; //이메일 인증키
    }

    // 회원가입 RESPONSE DTO
    @Data
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PostUserRes{
        private int userIdx;
        private String nickName;
        private String jwt;
    }

    // 로그인 REQUEST DTO
    @Data
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PostLoginReq{
        private String email;
        private String password;
    }

    // 로그인 RESPONSE DTO
    @Data
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PostLoginRes{
        private int userIdx;
        private String jwt;
    }

    // 전체유저 조회 RESPONSE DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class GetUserRes{
        private int userIdx;
        private String nickName;
        private String email;
        private String password;
        private String profileImgUrl;
        private String status;
        //    private String jwt;
    }

    // 전체유저 조회 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class DeleteUserReq{
        private int userIdx;
    }

    // 유저 닉네임 변경 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class UpdateNickNameReq{
        private int userIdx;
        private String nickName;
    }

    // 유저 프로필사진 변경 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class UpdateProfileImgUrlReq{
        private int userIdx;
        private String profileImgUrl;
    }


    // 유저 프로필사진 변경 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class UpdatePasswordReq {
        private int userIdx;
        private String password;
    }




}