package com.bit.kodari.dto;

import lombok.*;

 public class AccountDto {

    // 계좌 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Account {
        private int accountIdx;
        private String accountName;
        private int userIdx;
        private int marketIdx;
        private long property;
        private String status;
    }

    // 계좌 등록 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostAccountReq{
        private String accountName;
        private int userIdx;
        private int marketIdx;
        private long property;
    }

    // 계좌 등록 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PostAccountRes{
        private int accountIdx; // 추가해야함
        private String accountName;
        //    private String jwt;
    }

    // 유저 계좌 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetAccountRes{
        private int accountIdx;
        private String accountName;
        private int userIdx;
        private int marketIdx;
        private String property;
        private String status;
        //    private String jwt;
    }


    // 현금 자산 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetPropertyRes{
        private int accountIdx;
        private int userIdx; // 추가해야함
        private String property;
        private String status;
        //    private String jwt;
    }

    // 계좌 이름 수정 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchAccountNameReq{
        private String accountName;
        private int accountIdx;
        //private int marketIdx;
    }

    // 계좌 이름 수정 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PatchAccountNameRes{
        private int userIdx; // 추가해야함
        private int accountIdx; // 추가해야함
        private String accountName;
        //    private String jwt;
    }

    // 현금 자산 수정 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchPropertyReq{
        private int accountIdx;
        private long property;
        //    private String jwt;
    }

    // 현금 자산 수정 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PatchPropertyRes{
        private int accountIdx;
        private int userIdx; // 추가해야함
        private long property;
        //    private String jwt;
    }

    // 계좌 삭제 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchAccountDelReq{
        private int accountIdx;
        //    private String jwt;
    }

    // 계좌 삭제 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PatchAccountDelRes{
        private int accountIdx; // 추가해야함
        private int userIdx; // 추가해야함
        private String status;
        //    private String jwt;
    }

    // userIdx, marketIdx로 해당 거래소의 계좌 이름 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetAccountNameRes{
        private String accountName;
    }
}
