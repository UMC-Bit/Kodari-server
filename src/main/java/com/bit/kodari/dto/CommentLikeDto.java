package com.bit.kodari.dto;

import lombok.*;

public class CommentLikeDto {
    //토론장 댓글에 대한 좋아요 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentLike {
        private int commentLikeIdx;
        private int userIdx;
        private int postCommentIdx;
        private int like;
        private String status;
    }

    //토론장 댓글 좋아요 선택 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class RegisterCommentLikeReq{
        private int userIdx;
        private int postCommentIdx;
    }

    //토론장 댓글 좋아요 선택 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterCommentLikeRes{
        private int userIdx;
        //    private String jwt;
    }

//    //토론장 댓글 좋아요 수정
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    public static class PatchLikeReq{
//        private int commentLikeIdx;
//        private int userIdx;
//        private int postCommentIdx;
//        private int like;
//    }

    //토론장 댓글 좋아요 삭제
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeleteLikeReq{
        private int commentLikeIdx;
        private int like;
    }

    //토론장 댓글 좋아요 수정 시 중복 삭제
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeleteReq{
        private int commentLikeIdx;
    }

}
