package com.bit.kodari.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RepresentDto {

   // Represent 기본정보
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public static class Represent {
       private int representIdx;
       private int portIdx;
       private int coinIdx;
       private String status;
   }

   // 대표 코인 등록 REQUEST DTO
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public static class PostRepresentReq{
       private int portIdx;
       private int coinIdx;
   }

   // 대표 코인 등록 RESPONSE DTO
   @Data
   @Builder // 빌더 클래스 자동 생성
   public static class PostRepresentRes{
       private int representIdx;
       private int portIdx;
       private int coinIdx;
       //    private String jwt;
   }

    // 대표 코인 조회 RESPONSE DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetRepresentRes{
        private int representIdx;
        private int portIdx;
        private int coinIdx;
        private String status;
        //    private String jwt;
    }

    // 대표 코인 삭제 REQUEST DTO
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteRepresentReq{
        private int representIdx;
        //    private String jwt;
    }

    // 대표 코인 삭제 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class DeleteRepresentRes{
        private int representIdx;
        private int portIdx;
        private int coinIdx;
        private String status;
        //    private String jwt;
    }


}
