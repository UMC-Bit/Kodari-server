package com.bit.kodari.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RegisterCoinAlarmDto {

    @Data // @Getter, @Setter 모두 포함
    @NoArgsConstructor // 빈 생성자
    @AllArgsConstructor // 모든 인자 생성자
    public static class GetRegisterCoinAlarmRes{
        private int registerCoinAlarmIdx;
        private int userIdx;
        private int marketIdx;
        private int coinIdx;
        private double targetPrice;
        private String status;
    }
}
