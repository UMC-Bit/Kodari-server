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
    }

    //토론장 댓글 좋아요 선택 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterPostReportRes{
        private int respondent; //신고당하는 유저인덱스
        private int postReportIdx;
        //    private String jwt;
    }

    //토론장 댓글 좋아요 삭제 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeletePost{
        private int postIdx;
    }

    //토론장 댓글 좋아요 삭제 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostReportRes{
        private int userIdx; //신고하는 유저
        private int postReportIdx;
    }






}
