package com.bit.kodari.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ExchangeRateDto {

    //환율 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetExchangeRateRes{
       private int exchageRateIdx;
       private String money;
       private double exchagePrice;
    }
}
