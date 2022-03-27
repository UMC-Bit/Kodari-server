package com.bit.kodari.dto;

import lombok.*;

import java.util.List;

public class RegisterCoinAlarmDto {
    //코인 시세 알림 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class coinAlarm {
        private int registerCoinAlarmIdx;
        private int userIdx;
        private int marketIdx;
        private int coinIdx;
        private double targetPrice;
    }

    //코인 시세 알림 등록 REQUEST DTO
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class RegisterReq{
        private int userIdx;
        private int marketIdx;
        private int coinIdx;
        private double targetPrice; //지정가격
    }

    //코인 시세 알림 등록 RESPONSE DTO
    @Data
    @Builder // 빌더 클래스 자동 생성
    public static class RegisterRes{
        private int userIdx;
        //    private String jwt;
    }

    //코인 시세 알림 수정
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class PatchCoinAlarmReq{
        private int registerCoinAlarmIdx;
        private double targetPrice;
    }

    //코인 시세 알림 삭제
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미스의 모든 멤버 변수(email, password, nickName 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
    public static class PatchDeleteReq{
        private int registerCoinAlarmIdx;
    }

    //유저별 코인 시세 알림 조회
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetUserCoinAlarmRes{
        private int userIdx;
        private List<GetMarketRes> marketList;

    }

    //마켓 조회
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetMarketRes{
        private int marketIdx;
        private String marketName;
        private List<GetCoinRes> coinList;
    }

    //코인 조회
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetCoinRes{
        private int coinIdx;
        private String coinName; //코인 이름
        private String symbol; //코인 심볼
        private String coinImg; //코인 이미지
        private List<GetAlarmRes> alarmList;
    }

    //시세 알람 조회
    @Data
    @AllArgsConstructor // 해당 클래ame, profileImage)를 받는 생성자를 생성
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetAlarmRes{
        private int registerCoinAlarmIdx; //코인 시세 알림 인덱스
        private double targetPrice; //지정 가격
    }

}
