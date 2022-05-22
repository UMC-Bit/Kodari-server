package com.bit.kodari.service;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.RegisterCoinAlarmDto;
import com.bit.kodari.repository.registerCoinAlarm.RegisterCoinAlarmRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RegisterCoinAlarmService {
    @Autowired
    RegisterCoinAlarmRepository registerCoinAlarmRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;


    /*
    특정 코인 지정가격 조회: marketIdx, coinIdx 로 조회
     */
    @Transactional
    public List<RegisterCoinAlarmDto.GetRegisterCoinAlarmRes> getRegisterCoinAlarmPriceByMarketIdxCoinIdx(int marketIdx, int coinIdx) throws BaseException {
        List<RegisterCoinAlarmDto.GetRegisterCoinAlarmRes> getRegisterCoinAlarmRes = registerCoinAlarmRepository.getRegisterCoinAlarmPriceByMarketIdxCoinIdx(marketIdx,coinIdx);
        // null 예외처리
        if(getRegisterCoinAlarmRes.size()==0){
            //throw new BaseException(BaseResponseStatus.GET_REGISTERCOINALARM_NOT_EXISTS);
            System.out.println("코인 지정가격이 없습니다.");
        }

        try{
            return getRegisterCoinAlarmRes;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
