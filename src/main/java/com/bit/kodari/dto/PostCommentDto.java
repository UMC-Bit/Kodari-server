package com.bit.kodari.dto;

import lombok.*;

import java.util.List;

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


    //토론장 댓글과 관련된 댓글 좋아요 삭제
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class GetCommentLikeDeleteRes{
        private int commentLikeIdx;
    }

    //토론장 댓글 유저로 postCommentIdx 받아오기
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetPostCommentIdxRes{
        private int postCommentIdx;

    }

    //토론장 댓글 유저로 postCommentIdx 받아오기
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetPostIdx{
        private int postIdx;

    }



    //토론장 게시글 댓글조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetCommentsRes{
        private String content;
        private String time;
        private List<GetPostsRes> postList; //관련된 게시글

    }

    //토론장 게시글 조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetPostsRes{
        private int postIdx;
        private String symbol;
        private String profileImgUrl; //유저 프로필
        private String nickName; //유저 닉네임
        private String content; //게시글 내용
        private String time; //게시글 시간
        private int like = 0;
        private int dislike = 0;
        private int comment_cnt; // 댓글 수
    }

//
//    //토론장 게시글 댓글조회
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    public static class GetPostCommentRes{
//        private String nickName;
//        private String profileImgUrl;
//        private String content;
//        private String time;
//        private int like;
//        private boolean checkWriter; //게시글 유저 확인
//
//    }
//
//    //토론장 게시글별 댓글 수 조회
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class GetCommentCntRes{
//        private int postCommentIdx;
//    }
//
//    //토론장 댓글 유저확인
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    public static class GetCommentUserRes{
//        private int userIdx;
//
//    }
//

}
