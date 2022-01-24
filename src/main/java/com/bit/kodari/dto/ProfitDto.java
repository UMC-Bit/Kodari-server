package com.bit.kodari.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ProfitDto {

    // 수익 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Profit{
        private int profitIdx;
        private int accountIdx;// Account.accountIdx
        private double earning; // 전날 대비 수익 금액
        private double profitRate; // 전날 대비 수익률
        private String status;
    }

    // 수익 생성 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class PostProfitReq{
        private int accountIdx;// Account.accountIdx
        private double profitRate; // 전날 대비 수익률
        private double earning; // 수익금

    }


    // 수익 생성 RESPONSE DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class PostProfitRes{
        private int profitIdx;
        private int accountIdx;// Account.accountIdx
//        private double totalProperty; // 현재 총 자산
        private double profitRate; // 전날 대비 수익률
        private double earning; // 전 날 대비 수익금
        private String status;
    }

    // 수익내역 조회 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class GetProfitReq{
        private int accountIdx;// Account.accountIdx
    }


    // 수익내역 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class GetProfitRes{
        private int ProfitIdx;
        private int accountIdx;// Account.accountIdx
        //private double totalProperty; // 현재 총 자산
        private double profitRate; // 총 수익률
        private String earning; //  총 손익금
        private String status;
        private String createAt; // 수익 생성 시각
    }


    // 현재 코인 총자산 내역 조회 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class GetCurCoinTotalPropertyReq{
        private int accountIdx;// Account.accountIdx
    }


    // 현재 코인 총자산 내역 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class GetCurCoinTotalPropertyRes{
        private int accountIdx;// Account.accountIdx
        private double curCoinTotalProperty; // 현재 총 자산
    }

    // 소유코인의 코인심볼 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class GetCoinSymbolRes{
        private double amount; // 코인 갯수
        private String symbol; // 코인 심볼
        private double Property; // 현재 총 현금
        private double totalProperty; // 현재 총 자산
    }


    // 수익내역 삭제 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class PatchStatusReq{
        private int profitIdx;
    }


}
