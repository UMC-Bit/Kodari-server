package com.bit.kodari.dto;

import lombok.*;


public class PostDto {
    //토론장 게시글 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        private int postIdx;
        private int boardIdx;
        private int userIdx;
        private String content;
        private String status;
    }

    //토론장 게시글 작성 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class RegisterReq{
        private int boardIdx;
        private int userIdx;
        private String content;
    }

    //토론장 게시글 작성 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterRes{
        private int userIdx;
        //    private String jwt;
    }

    //토론장 게시글 수정
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class PatchPostReq{
        private int postIdx;
        private int userIdx;
        private String content;
    }

    //토론장 게시글 삭제
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class PatchDeleteReq{
        private int postIdx;
        private int userIdx;
    }

    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class CommentDeleteReq{
        private int postIdx;
        private int postCommentIdx;

    }

    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class LikeDeleteReq{
        private int postIdx;
        private int postLikeIdx;

    }


    //토론장 게시글 조회
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetPostRes{
        private String boardName;
        private String nickName;
        private String content;
    }






}
