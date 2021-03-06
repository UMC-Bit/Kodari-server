package com.bit.kodari.service;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.RegisterCoinAlarmDto;
import com.bit.kodari.repository.registerCoinAlarm.RegisterCoinAlarmRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class RegisterCoinAlarmService {
    @Autowired
    RegisterCoinAlarmRepository registerCoinAlarmRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public RegisterCoinAlarmService(RegisterCoinAlarmRepository registerCoinAlarmRepository, JwtService jwtService) {
        this.registerCoinAlarmRepository = registerCoinAlarmRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // 코인 시세 알림 등록(POST)
    @Transactional
    public RegisterCoinAlarmDto.RegisterRes insertCoinAlarm(RegisterCoinAlarmDto.RegisterReq registerReq) throws BaseException {
        int userIdx = registerReq.getUserIdx();
        int marketIdx = registerReq.getMarketIdx();
        int coinIdx = registerReq.getCoinIdx();
        double targetPrice = registerReq.getTargetPrice();
        boolean exist = registerCoinAlarmRepository.getExistAlarm(userIdx, marketIdx, coinIdx, targetPrice);
        int alarm_count = registerCoinAlarmRepository.getAlarmByCoinIdx(userIdx, marketIdx, coinIdx);
        int coin_count = registerCoinAlarmRepository.getCoinByMarketIdx(userIdx, marketIdx);
        if(coin_count > 2 && alarm_count == 3) { //코인은 세개만 등록 가능
            throw new BaseException(FAIL_REGISTER_COIN_ALARM); //코인 등록은 최대 3개까지 가능
        }
        if(alarm_count > 2) { //코인 당 알림 최대 3개까지 등록
            throw new BaseException(FAIL_REGISTER_ALARM); //지정가 알림 등록은 최대 3개까지 가능
        }
        else if(exist) {
            throw new BaseException(EQUALS_REGISTER_ALARM); //같은값의 알림이 존재
        }
        else if(targetPrice == 0.0) { //숫자 이외의 문자는 등록 불가능
            throw new BaseException(EMPTY_PRICE);
        }
        try {
            return registerCoinAlarmRepository.insert(registerReq);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //코인 시세 알림 수정
    @Transactional
    public void modifyCoinAlarm(RegisterCoinAlarmDto.PatchCoinAlarmReq patch) throws BaseException{
        double targetPrice = patch.getTargetPrice();
        int userIdx = registerCoinAlarmRepository.getUserIdxByRegisterCoinAlarmIdx(patch.getRegisterCoinAlarmIdx());
        int marketIdx = registerCoinAlarmRepository.getMarketIdxByRegisterCoinAlarmIdx(patch.getRegisterCoinAlarmIdx());
        int coinIdx = registerCoinAlarmRepository.getCoinIdxByRegisterCoinAlarmIdx(patch.getRegisterCoinAlarmIdx());
        boolean exist = registerCoinAlarmRepository.getExistAlarm(userIdx, marketIdx, coinIdx, targetPrice);
        if(targetPrice == 0.0) { //숫자 이외의 문자는 등록 불가능
            throw new BaseException(EMPTY_PRICE);
        }
        else if(exist) {
            throw new BaseException(EQUALS_REGISTER_ALARM); //같은값의 알림이 존재
        }
        try {
            int result = registerCoinAlarmRepository.modifyCoinAlarm(patch);
            if(result == 0){ // 0이면 에러가 발생
                throw new BaseException(MODIFY_FAIL_ALARM);
            }

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //코인 시세 알림 삭제
    @Transactional
    public void modifyAlarmStatus(RegisterCoinAlarmDto.PatchDeleteReq delete) throws BaseException{
        try{
            int result = registerCoinAlarmRepository.modifyAlarmStatus(delete);
            if(result == 0){ // 0이면 에러가 발생
                throw new BaseException(DELETE_FAIL_ALARM);
            }

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유저별 코인 시세 알림 조회
    @Transactional
    public RegisterCoinAlarmDto.GetUserCoinAlarmRes getAlarmsByUserIdx(int userIdx) throws BaseException {
        try {
            RegisterCoinAlarmDto.GetUserCoinAlarmRes getAlarmsRes = registerCoinAlarmRepository.getAlarms(userIdx);
            return getAlarmsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }




}
