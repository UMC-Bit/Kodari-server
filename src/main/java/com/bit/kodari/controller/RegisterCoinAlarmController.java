package com.bit.kodari.controller;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.RegisterCoinAlarmDto;
import com.bit.kodari.repository.registerCoinAlarm.RegisterCoinAlarmRepository;
import com.bit.kodari.service.RegisterCoinAlarmService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;


@Slf4j
@RestController
@RequestMapping("/registercoinalarm")
public class RegisterCoinAlarmController {
    @Autowired
    RegisterCoinAlarmService registerCoinAlarmService;
    @Autowired
    RegisterCoinAlarmRepository registerCoinAlarmRepository;
    @Autowired
    private final JwtService jwtService;

    public RegisterCoinAlarmController(RegisterCoinAlarmService registerCoinAlarmService, JwtService jwtService) {
        this.registerCoinAlarmService = registerCoinAlarmService;
        this.jwtService = jwtService;
    }

    /*
    코인 시세 알림 등록
  */
    @PostMapping(value="/register")
    @ApiOperation(value = "코인 시세 알림 등록", notes = "코인 시세 알림 등록함.")
    public BaseResponse<RegisterCoinAlarmDto.RegisterRes> createCoinAlarm(@RequestBody RegisterCoinAlarmDto.RegisterReq registerReq){
        int userIdx = registerReq.getUserIdx();
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            RegisterCoinAlarmDto.RegisterRes registerRes = registerCoinAlarmService.insertCoinAlarm(registerReq);
            return new BaseResponse<>(registerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        코인 시세 알림 수정
     */
    @PatchMapping("/update/{registerCoinAlarmIdx}")
    @ApiOperation(value = "코인 시세 알림 수정", notes = "코인 시세 알림 지정 가격 수정함.")
    public BaseResponse<String> updateRegisterCoinAlarm(@PathVariable("registerCoinAlarmIdx") int registerCoinAlarmIdx, @RequestBody RegisterCoinAlarmDto.PatchCoinAlarmReq registerAlarm){
        int userIdx = registerCoinAlarmRepository.getUserIdxByRegisterCoinAlarmIdx(registerCoinAlarmIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            RegisterCoinAlarmDto.PatchCoinAlarmReq patchReq = new RegisterCoinAlarmDto.PatchCoinAlarmReq(registerCoinAlarmIdx, registerAlarm.getTargetPrice());
            registerCoinAlarmService.modifyCoinAlarm(patchReq);
            String result = "코인 시세 알림이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        코인 시세 알림 삭제
     */
    @PatchMapping("/status/{registerCoinAlarmIdx}")
    @ApiOperation(value = "코인 시세 알림 삭제", notes = "등록한 코인 시세 알림 삭제함.")
    public BaseResponse<String> modifyAlarmStatus(@PathVariable("registerCoinAlarmIdx") int registerCoinAlarmIdx) {
        int userIdx = registerCoinAlarmRepository.getUserIdxByRegisterCoinAlarmIdx(registerCoinAlarmIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            RegisterCoinAlarmDto.PatchDeleteReq patchReq = new RegisterCoinAlarmDto.PatchDeleteReq(registerCoinAlarmIdx);
            registerCoinAlarmService.modifyAlarmStatus(patchReq);
            String result = "코인 시세 알림이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        유저별 코인 시세 알림 조회
     */
    @GetMapping("/alarm")
    @ApiOperation(value = "유저별 코인 시세 알림 조회", notes = "유저별 코인 시세 알림을 조회함")
    public BaseResponse<RegisterCoinAlarmDto.GetUserCoinAlarmRes> getCoinAlarm(@RequestParam int userIdx) {
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            RegisterCoinAlarmDto.GetUserCoinAlarmRes getAlarmsRes = registerCoinAlarmService.getAlarmsByUserIdx(userIdx);
            return new BaseResponse<>(getAlarmsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
