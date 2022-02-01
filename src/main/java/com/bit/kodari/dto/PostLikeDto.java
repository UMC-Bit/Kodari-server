package com.bit.kodari.dto;

import lombok.*;

public class PostLikeDto {
    //토론장 게시글에 대한 좋아요/싫어요 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostLike {
        private int postLikeIdx;
        private int userIdx;
        private int postIdx;
        private int likeType;
        private String status;
    }

    //토론장 게시글 좋아요/싫어요 선택 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class RegisterLikeReq{
        private int userIdx;
        private int postIdx;
        private int likeType;
    }

    //토론장 게시글 좋아요/싫어요 선택 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterLikeRes{
        private int userIdx;
        private int postIdx;
        private int likeType;
        //    private String jwt;
    }

    //토론장 댓글 좋아요 삭제 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostLikeReq{
        private int userIdx;
        private int postLikeIdx;
    }
    //토론장 댓글 좋아요 삭제 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostLikeRes{
        private int userIdx;
        private int postLikeIdx;
    }

    //토론장 게시글 좋아요/싫어요 수정
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PatchLikeReq{
        private int postLikeIdx;
        private int userIdx;
        private int postIdx;
        private int likeType;
    }

    //토론장 게시글 좋아요/싫어요 삭제
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeleteLikeReq{
        private int postLikeIdx;
    }

    //토론장 게시글 좋아요/싫어요 수정 시 중복 삭제
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeleteReq{
        private int postLikeIdx;
    }



    //토론장 게시글별 좋아요 조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetLikeRes{
        private int likeType;
    }

    //토론장 게시글별 싫어요 조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetDislikeRes{
        private int likeType;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetUserIdx {
        private int userIdx;
    }


}
