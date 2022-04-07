package com.bit.kodari.dto;

import lombok.*;

public class FcmDto {

    // 모바일 알림 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostSNSReq{
        private int userIdx;
    }
}
