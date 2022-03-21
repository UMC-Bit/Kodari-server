package com.bit.kodari.dto;

import lombok.*;

import java.util.List;


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
        private int coinIdx;
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
        private String content;
    }

    //토론장 게시글 삭제
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class PatchDeleteReq{
        private int postIdx;
    }

    //토론장 게시글 댓글 삭제
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class GetCommentDeleteRes{
        private int postCommentIdx;
    }

    //토론장 댓글 좋아요 삭제
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class GetCommentLikeDeleteRes{
        private int commentLikeIdx;
    }

    //토론장 게시글 좋아요/싫어요 삭제
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class GetLikeDeleteRes{
        private int postLikeIdx;
    }


    //토론장 게시글 답글 삭제
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class GetReplyDeleteRes{
        private int postReplyIdx;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetUserIdxRes{
        private int userIdx;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetStatusRes{
        private String status;
    }



    //토론장 게시글 조회
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetPostRes{
        private int postIdx; //게시글 인덱스
        private String symbol; //코인 심볼
        private String nickName; //유저 닉네임
        private String profileImgUrl; //유저 프로필
        private String content; //게시글 내용
        private String time; // 게시글 시간
        private int like = 0;
        private int dislike = 0;
        private boolean checkPostLike; // 게시글 좋아요 유저 확인
        private boolean checkPostDislike; // 게시글 싫어요 유저 확인
        private int comment_cnt;
    }

    //토론장 게시글별 조회
    @Data
    @AllArgsConstructor // 해당 클래스, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetUserPostRes{
        private int postIdx;
        private String symbol; //코인 심볼
        private String nickName; //유저 닉네임
        private String profileImgUrl; //유저 프로필
        private String content; //게시글 내용
        private String time; //게시글 시간
        private int like = 0;
        private int dislike = 0;
        private int comment_cnt; //댓글 수
        private boolean checkWriter; // 게시글 유저 확인
        private boolean checkPostLike; // 게시글 좋아요 유저 확인
        private boolean checkPostDislike; // 게시글 싫어요 유저 확인
        private List<GetCommentRes> commentList; //댓글과 답글 리스트



    }

    //토론장 게시글 댓글조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetCommentRes{
        private int userIdx;
        private int postCommentIdx;
        private String profileImgUrl;
        private String nickName;
        private String content;
        private String time; //댓글 시간
        private int like;
        private boolean checkCommentWriter; // 댓글 유저 확인
        private String comment_status; // 댓글 삭제 여부 확인
        private boolean checkCommentLike; //댓글 좋아요 유저 확인
        private List<GetReplyRes> replyList; //답글 리스트
    }

    //토론장 게시글 유저별 답글조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetReplyRes{
        private int userIdx;
        private int postReplyIdx;
        private String profileImgUrl;
        private String nickName;
        private String content;
        private String time; //답글 시간
        private boolean checkReplyWriter; // 답글 유저 확인
        private String reply_status; //답글 삭제 여부 확인
    }




}
