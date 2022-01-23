package com.bit.kodari.dto;

import lombok.*;

public class PostReplyDto {

    //토론장 게시글 답글 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostReply {
        private int postReplyIdx;
        private int userIdx;
        private int postCommentIdx;
        private String content;
        private String status;
    }

    //토론장 게시글 답글 작성 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RegisterReplyReq{
        private int userIdx;
        private int postCommentIdx;
        private String content;
    }

    //토론장 게시글 답글 작성 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterReplyRes{
        private int userIdx;
        //    private String jwt;
    }

    //토론장 게시글 답글 수정
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PatchReplyReq{
        private int postReplyIdx;
        private int userIdx;
        private int postCommentIdx;
        private String content;
    }

    //토론장 게시글 답글 삭제
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PatchReplyDeleteReq{
        private int postReplyIdx;
        private int userIdx;
        private int postCommentIdx;
    }

    //토론장 게시글 유저별 답글조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetReplyRes{
        private String profileImgUrl;
        private String nickName;
        private String content;
    }


    //토론장 댓글별 답글 수 조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetReplyCntRes{
        private int postReplyIdx;
    }


}
