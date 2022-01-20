package com.bit.kodari.dto;

import lombok.*;

public class PostCommentDto {
    //토론장 게시글 댓글 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostComment {
        private int postCommentIdx;
        private int userIdx;
        private int postIdx;
        private String content;
        private String status;
    }

    //토론장 게시글 댓글 작성 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RegisterCommentReq{
        private int userIdx;
        private int postIdx;
        private String content;
    }

    //토론장 게시글 댓글 작성 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterCommentRes{
        private int userIdx;
        //    private String jwt;
    }

    //토론장 게시글 댓글 수정
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PatchCommentReq{
        private int postCommentIdx;
        private int userIdx;
        private int postIdx;
        private String content;
    }

    //토론장 게시글 댓글 삭제
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PatchDeleteReq{
        private int postCommentIdx;
        private int userIdx;
        private int postIdx;
    }

    //토론장 게시글 댓글조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetCommentRes{
        private String nickName;
        private int likeCnt;
        private String content;

    }

}
