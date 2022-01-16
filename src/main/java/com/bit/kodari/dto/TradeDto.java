package com.bit.kodari.dto;

import lombok.*;


public class TradeDto {
    // 거래내영 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Trade {
        private int tradeIdx;
        private int portIdx; // Portfolio.portIdx
        private int coinIdx; // Coin.coinIdx
        private double price; // 코인 가격
        private double amount; // 코인 갯수
        private double fee; // 코인 수수료
        private String category; //매수 or 매도 : “buy”, “sell”
        private String memo; //메모( 유저가 직접 기록)
        private String date; //거래일자(YYYY-MM-DD)
        private String status; // "active" , "inactive"
    }


    // 거래내역 생성 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class PostTradeReq{
        private int portIdx; // Portfolio.portIdx
        private int coinIdx; // Coin.coinIdx
        private double price; // 코인 가격
        private double amount; // 코인 갯수
        private double fee; // 코인 수수료
        private String category; //매수 or 매도 : “buy”, “sell”
        private String memo; //메모( 유저가 직접 기록)
        //private String date; //거래일자(YYYY-MM-DD)
    }


    // 거래내역 생성 RESPONSE DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class PostTradeRes{
        private int tradeIdx;
//        private int portIdx;
//        private int coinIdx;
    }


    // 거래내역 조회 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래스 인자를 받는 생성자를 생성
    @NoArgsConstructor  // 해당 클래스의 파라미스의 모든 멤버 변수 없는 생성자를 생성
    public static class GetTradeReq{
        private int portIdx; // Portfolio.portIdx
        private int coinIdx; // Coin.coinIdx
        //private String date; //거래일자(YYYY-MM-DD)
    }


    // 전체유저 조회 RESPONSE DTOc
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class GetTradeRes{
        private int tradeIdx;
        private String coinName; //
        private double price; // 코인 가격
        private double amount; // 코인 갯수
        private double fee; // 코인 수수료
        private String category; //매수 or 매도 : “buy”, “sell”
        private String memo; //메모( 유저가 직접 기록)
        private String date; //거래일자(YYYY-MM-DD)
        private String status; // "active" , "inactive"
        //    private String jwt;
    }


    // 거래내역 코인가격 수정 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PatchPriceReq{
        private int tradeIdx;
        private double price; // 코인 가격
    }


    // 거래내역 코인 갯수 수정 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PatchAmountReq{
        private int tradeIdx;
        private double amount;
    }



    // 거래내역 수수료 수정 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PatchFeeReq{
        private int tradeIdx;
        private double fee;
    }



    // 거래내역 코인 갯수 수정 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PatchCategoryReq{
        private int tradeIdx;
        private String category ;
    }


    // 거래내역 메모 수정 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PatchMemoReq{
        private int tradeIdx;
        private String memo ;
    }



    // 거래내역 메모 수정 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PatchDateReq{
        private int tradeIdx;
        private String date ;
    }



    // 거래내역 삭제 REQUEST DTO
    @Data // @Getter @Setter 포함
    @AllArgsConstructor // 인자 포함한 생성자 생성
    @NoArgsConstructor // 인자 없는 생성자 생성
    public static class PatchStatusReq{
        private int tradeIdx;
    }
}
