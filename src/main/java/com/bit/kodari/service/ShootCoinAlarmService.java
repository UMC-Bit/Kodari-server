package com.bit.kodari.service;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.ShootCoinAlarmDto;
import com.bit.kodari.repository.shootcoinalarm.ShootCoinAlarmRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class ShootCoinAlarmService {
    @Autowired
    ShootCoinAlarmRepository shootCoinAlarmRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public ShootCoinAlarmService(ShootCoinAlarmRepository shootCoinAlarmRepository, JwtService jwtService) {
        this.shootCoinAlarmRepository = shootCoinAlarmRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // 코인 시세 알림 등록(POST)
    @Transactional
    public ShootCoinAlarmDto.RegisterRes insertCoinAlarm(ShootCoinAlarmDto.RegisterReq registerReq) throws BaseException {
        int userIdx = registerReq.getUserIdx();
        int marketIdx = registerReq.getMarketIdx();
        int coinIdx = registerReq.getCoinIdx();
        double growth = registerReq.getGrowth();
        double decline = registerReq.getDecline();
        boolean exist = shootCoinAlarmRepository.getExistAlarm(userIdx, marketIdx, coinIdx, growth, decline);
        int alarm_count = shootCoinAlarmRepository.getAlarmByCoinIdx(userIdx, marketIdx, coinIdx);
        int coin_count = shootCoinAlarmRepository.getCoinByMarketIdx(userIdx, marketIdx);
        if (coin_count > 2 && alarm_count == 3) { //코인은 세개만 등록 가능
            throw new BaseException(FAIL_REGISTER_COIN_ALARM); //코인 등록은 최대 3개까지 가능
        }
        if(alarm_count > 2) { //코인 당 알림 최대 3개까지 등록
            throw new BaseException(FAIL_REGISTER_ALARM); //폭락, 폭등 알림 등록은 최대 3개까지 가능
        }
        else if(growth < 0.0 || growth > 10000.0) {
            throw new BaseException(ASCENT_RATE_IS_LIMITED); //상승률은 0~10000% 이내
        }
        else if(decline < 0.0 || decline > 100.0) {
            throw new BaseException(DECLINE_RATE_IS_LIMITED); //하락률은 0~100% 이내
        }
        else if(growth == 0.0 && decline == 0.0) { //0%로 설정된 알림은 반영되지 않음
            throw new BaseException(NOT_REFLECTED_IN_NOTIFICATIONS);
        }
        else if(exist) {
            throw new BaseException(EQUALS_REGISTER_ALARM); //같은값의 알림이 존재
        }
        try {
            return shootCoinAlarmRepository.insert(registerReq);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //코인 시세 알림 수정
    @Transactional
    public void modifyCoinAlarm(ShootCoinAlarmDto.PatchCoinAlarmReq patch) throws BaseException{
        double growth = patch.getGrowth();
        double decline = patch.getDecline();
        int userIdx = shootCoinAlarmRepository.getUserIdxByShootCoinAlarmIdx(patch.getShootCoinAlarmIdx());
        int marketIdx = shootCoinAlarmRepository.getMarketIdxByShootCoinAlarmIdx(patch.getShootCoinAlarmIdx());
        int coinIdx = shootCoinAlarmRepository.getCoinIdxByShootCoinAlarmIdx(patch.getShootCoinAlarmIdx());
        boolean exist = shootCoinAlarmRepository.getExistAlarm(userIdx, marketIdx, coinIdx, growth, decline);
        if(growth == 0.0 && decline == 0.0) { //0%로 설정된 알림은 반영되지 않음
            throw new BaseException(NOT_REFLECTED_IN_NOTIFICATIONS);
        }
        else if(growth < 0.0 || growth > 10000.0) {
            throw new BaseException(ASCENT_RATE_IS_LIMITED); //상승률은 0~10000% 이내
        }
        else if(decline < 0.0 || decline > 100.0) {
            throw new BaseException(DECLINE_RATE_IS_LIMITED); //하락률은 0~100% 이내
        }
        else if(exist) {
            throw new BaseException(EQUALS_REGISTER_ALARM); //같은값의 알림이 존재
        }
        try {
            int result = shootCoinAlarmRepository.modifyCoinAlarm(patch);
            if(result == 0){ // 0이면 에러가 발생
                throw new BaseException(MODIFY_FAIL_ALARM);
            }

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //코인 시세 알림 삭제
    @Transactional
    public void modifyAlarmStatus(ShootCoinAlarmDto.PatchDeleteReq delete) throws BaseException{
        try{
            int result = shootCoinAlarmRepository.modifyAlarmStatus(delete);
            if(result == 0){ // 0이면 에러가 발생
                throw new BaseException(DELETE_FAIL_ALARM);
            }

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유저별 폭락, 폭등 알림 조회
    @Transactional
    public ShootCoinAlarmDto.GetUserCoinAlarmRes getAlarmsByUserIdx(int userIdx) throws BaseException {
        try {
            ShootCoinAlarmDto.GetUserCoinAlarmRes getAlarmsRes = shootCoinAlarmRepository.getAlarms(userIdx);
            return getAlarmsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }




}
