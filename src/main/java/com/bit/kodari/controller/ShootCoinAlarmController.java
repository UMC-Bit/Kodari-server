package com.bit.kodari.controller;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.ShootCoinAlarmDto;
import com.bit.kodari.repository.shootcoinalarm.ShootCoinAlarmRepository;
import com.bit.kodari.service.ShootCoinAlarmService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;


@Slf4j
@RestController
@RequestMapping("/shootcoinalarm")
public class ShootCoinAlarmController {
    @Autowired
    ShootCoinAlarmService shootCoinAlarmService;
    @Autowired
    ShootCoinAlarmRepository shootCoinAlarmRepository;
    @Autowired
    private final JwtService jwtService;

    public ShootCoinAlarmController(ShootCoinAlarmService shootCoinAlarmService, JwtService jwtService) {
        this.shootCoinAlarmService = shootCoinAlarmService;
        this.jwtService = jwtService;
    }

    /*
    폭락,폭등 알림 등록
  */
    @PostMapping(value="/register")
    @ApiOperation(value = "폭락, 폭등 알림 등록", notes = "폭락, 폭등 알림 등록함.")
    public BaseResponse<ShootCoinAlarmDto.RegisterRes> createCoinAlarm(@RequestBody ShootCoinAlarmDto.RegisterReq registerReq){
        int userIdx = registerReq.getUserIdx();
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            ShootCoinAlarmDto.RegisterRes registerRes = shootCoinAlarmService.insertCoinAlarm(registerReq);
            return new BaseResponse<>(registerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        폭락, 폭등 알림 수정
     */
    @PatchMapping("/update/{shootCoinAlarmIdx}")
    @ApiOperation(value = "코인 시세 알림 수정", notes = "코인 시세 알림 지정 가격 수정함.")
    public BaseResponse<String> updateRegisterCoinAlarm(@PathVariable("shootCoinAlarmIdx") int shootCoinAlarmIdx, @RequestBody ShootCoinAlarmDto.PatchCoinAlarmReq registerAlarm){
        int userIdx = shootCoinAlarmRepository.getUserIdxByShootCoinAlarmIdx(shootCoinAlarmIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            ShootCoinAlarmDto.PatchCoinAlarmReq patchReq = new ShootCoinAlarmDto.PatchCoinAlarmReq(shootCoinAlarmIdx, registerAlarm.getGrowth(), registerAlarm.getDecline());
            shootCoinAlarmService.modifyCoinAlarm(patchReq);
            String result = "폭락, 폭등 알림이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
      폭락, 폭등 알림 삭제
  */
    @PatchMapping("/status/{shootCoinAlarmIdx}")
    @ApiOperation(value = "폭락, 폭등 알림 삭제", notes = "등록한 폭락, 폭등 알림 삭제함.")
    public BaseResponse<String> modifyAlarmStatus(@PathVariable("shootCoinAlarmIdx") int shootCoinAlarmIdx) {
        int userIdx = shootCoinAlarmRepository.getUserIdxByShootCoinAlarmIdx(shootCoinAlarmIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            ShootCoinAlarmDto.PatchDeleteReq patchReq = new ShootCoinAlarmDto.PatchDeleteReq(shootCoinAlarmIdx);
            shootCoinAlarmService.modifyAlarmStatus(patchReq);
            String result = "폭락, 폭등 알림이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        유저별 폭락, 폭등 알림 조회
     */
    @GetMapping("/alarm")
    @ApiOperation(value = "유저별 폭락, 폭등 알림 조회", notes = "유저별 폭락, 폭등 알림을 조회함")
    public BaseResponse<ShootCoinAlarmDto.GetUserCoinAlarmRes> getCoinAlarm(@RequestParam int userIdx) {
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            ShootCoinAlarmDto.GetUserCoinAlarmRes getAlarmsRes = shootCoinAlarmService.getAlarmsByUserIdx(userIdx);
            return new BaseResponse<>(getAlarmsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }




}
