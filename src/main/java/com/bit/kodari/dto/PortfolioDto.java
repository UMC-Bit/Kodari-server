package com.bit.kodari.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class PortfolioDto {
    //Portfolio 기본 정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Portfolio {
        private int portIdx;
        private int userIdx;
        private int accountIdx;
        private String status;
    }

    //포트폴리오 생성 Request Dto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostPortfolioReq{
        private int userIdx;
        private int accountIdx;
    }

    //포트폴리오 생성 Response Dto
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PostPortfolioRes{
        private int portIdx;
        private int userIdx;
        private int accountIdx;
    }

    // 포트폴리오의 대표코인 조회 RESPONSE DTO
    @Data
    @Builder
    public static class GetRepresentRes{
        private int coinIdx;
        //    private String jwt;
    }

    // 포트폴리오의 소유코인 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetPortfolioRes{
        private int portIdx;
        private int accountIdx;
        private String accountName;
        private double property;
        private double totalProperty;
        private int userIdx;
        private String marketName;
        private List<UserCoinDto.UserCoin> userCoinList;
        private List<RepresentDto.GetRepresentRes> representCoinList;
        private List<ProfitDto.GetProfitRes> profitList;
        //대표코인 리스트 - 해당 포트폴리오의
        //    private String jwt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetPortSumByMarketRes{
        private int userIdx;
        private String nickName;
        private List<PortfolioDto.GetPortSumRes> portSumList1;
        private List<PortfolioDto.GetPortSumRes> portSumList2;
    }

    // 거래소별 포트폴리오 갯수 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetPortSumRes{
        private int marketIdx;
        private String marketName;
        private int portSum;
    }


    //포트폴리오 삭제 Request Dto
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatchPortfolioDelReq{
        private int portIdx;
        //    private String jwt;
    }

    // 대표 코인 삭제 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteRepresentReq{
        private int portIdx;
        //    private String jwt;
    }

    //포트폴리오 삭제 Response Dto
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class PatchPortfolioDelRes{
        private int portIdx;
        private int userIdx;
        private int accountIdx;
        private String status;
        //    private String jwt;
    }

    // 모든 포트폴리오 userIdx, accountIdx RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetAllPortfolioRes{
        private int userIdx;
        private int accountIdx;
    }

    // userIdx로 portfolioIdx List로 받아오는 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetAllPortIdxRes{
        private int portIdx;
    }

    // portIdx로 userCoinIdx List로 받아오는 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetUserCoinIdxRes{
        private int userCoinIdx;
    }
}
