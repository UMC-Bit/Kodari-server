package com.bit.kodari.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CoinDto {
    //토론장 코인 조회
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetCoinRes{
        private String coinName; //코인 이름
        private String symbol; //코인 심볼
        private String coinImg;
    }
}
