package com.bit.kodari.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserCoinDto {
    //UserCoin 기본 정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCoin {
        private int userCoinIdx;
        private int userIdx;
        private int coinIdx;
        private String coinName;
        private String symbol;
        private String coinImg;
        private String twitter;
        private int accountIdx;
        private double priceAvg;
        private double amount;
        private String status;
    }

    //소유 코인 생성 Request Dto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostUserCoinReq{
        private int userIdx;
        private int coinIdx;
        private int accountIdx;
        private double priceAvg;
        private double amount;
    }

    //소유 코인 생성 Response Dto
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PostUserCoinRes{
        private int userCoinIdx;
        private int coinIdx;
        //    private String jwt;
    }

    //소유 코인 수정 Request Dto
    //매수평단가 계산 계속해줘야함
    //trade에서 수정될 때 마다
    //통장 잔고
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchUserCoinReq{
        private int userCoinIdx;
        private double priceAvg;
        private double amount;
    }

    //소유 코인 수정 Response Dto
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PatchUserCoinRes{
        private int userCoinIdx;
        private int userIdx;
        private int coinIdx;
        private int accountIdx;
        private double priceAvg;
        private double amount;
    }

    //특정 코인 조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetUserCoinIdxRes{
        private int userCoinIdx;
        private int coinIdx;
        private String coinName;
        private String symbol;
        private String coinImg;
        private String twitter;
        private int userIdx;
        private double priceAvg;
        private double amount;
        private String status;
    }


    // 소유 코인 조회 Response Dto
    // coinIdx로 coinName 받아오는 것 필요함
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetUserCoinRes{
        private int portIdx;
        private int userCoinIdx;
        private int coinIdx;
        private String coinName;
        private String symbol;
        private String coinImg;
        private String twitter;
        private int userIdx;
        private double priceAvg;
        private double amount;
        private String status;
    }

    //소유 코인 삭제 Request Dto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchUserCoinDelReq{
        private int userCoinIdx;
        //    private String jwt;
    }

    //소유 코인 삭제 Response Dto
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PatchUserCoinDelRes{
        private int userCoinIdx;
        private int userIdx; // 추가해야함
        private String status;
        //    private String jwt;
    }

    //소유 코인 전체 삭제 Request Dto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchDelByUserIdxReq{
        private int userIdx;
        //    private String jwt;
    }

    //소유 코인 전체 삭제 Response Dto
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PatchDelByUserIdxRes{
        private int userCoinIdx;
        private int userIdx;
        private String status;
    }

    //Trade - 매수, 매도 계산 Request Dto
    //수수료 0.05%
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchBuySellReq{
        private int userCoinIdx;
        //private int userIdx;
        //private int coinIdx;
        //private String category;
        private double priceAvg;
        private double amount;
        //private double fee;
        //    private String jwt;
    }

    //Trade - 매수, 매도 계산 Request Dto
    //수수료 0.05%
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchBuySellRes{
        private int userIdx;
        private int coinIdx;
        private String category;
        private double price;
        private double amount;
        private double fee;
        private double priceAvg;
        //    private String jwt;
    }

}
