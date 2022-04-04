package com.bit.kodari.dto;

import lombok.*;

public class ReportDto {
    //토론장 게시글 신고기능 선택 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RegisterPostReportReq{
        private int postIdx;
        private int reporter; //신고하는 유저
        private String reason; //신고 사유
    }

    //토론장 게시글 신고기능 선택 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterPostReportRes{
        private int respondent; //신고당하는 유저인덱스
        private int postReportIdx;
        //    private String jwt;
    }

    //토론장 게시글 삭제 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeletePost{
        private int postIdx;
    }

    //토론장 게시글 신고기능
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostReportRes{
        private int userIdx; //신고하는 유저
    }

    //토론장 게시글 삭제 시 유저 report + 1
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserReportRes{
        private int userIdx; //신고당하는 유저
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


    //토론장 댓글 신고



    //토론장 댓글 신고기능 선택 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RegisterPostCommentReportReq{
        private int postCommentIdx;
        private int reporter; //신고하는 유저
        private String reason; //신고 사유
    }

    //토론장 댓글 신고기능 선택 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterPostCommentReportRes{
        private int respondent; //신고당하는 유저인덱스
        private int postCommentReportIdx;
        //    private String jwt;
    }

    //토론장 댓글 내용 변경(삭제) REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeletePostComment {
        private int postCommentIdx;
    }

    //토론장 댓글 신고기능
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostCommentReportRes{
        private int userIdx; //신고하는 유저
    }




    //토론장 답글 신고



    //토론장 답글 신고기능 선택 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RegisterPostReplyReportReq{
        private int postReplyIdx;
        private int reporter; //신고하는 유저
        private String reason; //신고 사유
    }

    //토론장 답글 신고기능 선택 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterPostReplyReportRes{
        private int respondent; //신고당하는 유저인덱스
        private int postReportIdx;
        //    private String jwt;
    }

    //토론장 답글 삭제 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeletePostReply{
        private int postReplyIdx;
    }

    //토론장 답글 신고기능
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostReplyReportRes{
        private int userIdx; //신고하는 유저
    }


    //토론장 유저 신고


    //토론장 유저 신고기능 선택 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RegisterPostUserReportReq{
        private int postIdx;
        private int reporter; //신고하는 유저
    }

    //토론장 유저 신고기능 선택 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterPostUserReportRes{
        private int respondent; //신고당하는 유저인덱스
        //    private String jwt;
    }

    //토론장 유저 신고기능
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostUserReportRes{
        private int userIdx; //신고하는 유저
    }







}
