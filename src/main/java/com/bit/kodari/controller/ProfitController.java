package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.ProfitDto;
import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.repository.profit.ProfitRepository;
import com.bit.kodari.repository.trade.TradeRepository;
import com.bit.kodari.service.ProfitService;
import com.bit.kodari.service.TradeService;
import com.bit.kodari.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/profits")
public class ProfitController {

    @Autowired
    private final ProfitService profitService;
    @Autowired
    private final JwtService jwtService; // JWT부분
    @Autowired
    private final ProfitRepository profitRepository;

    public ProfitController(ProfitService profitService, JwtService jwtService, ProfitRepository profitRepository) {
        this.profitService = profitService;
        this.profitRepository = profitRepository;
        this.jwtService = jwtService; // JWT부분
    }


    /**
     * [POST]
     */
    //수익 생성 API
    @ResponseBody
    @PostMapping("/post")
    @ApiOperation(value = "수익 생성할 포트폴리오,계좌", notes = "수익을 새로 등록함.")
    public BaseResponse<ProfitDto.PostProfitRes> createProfit(@RequestBody ProfitDto.PostProfitReq postProfitReq) throws IOException{

        int userIdx = profitRepository.getUserIdxByAccountIdx(postProfitReq.getAccountIdx()); // 계좌 인덱스로 유저인덱스 조회
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            // 수익 생성 요청
            ProfitDto.PostProfitRes postProfitRes = profitService.createProfit(postProfitReq);
            return new BaseResponse<>(postProfitRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * [GET]
     */
    // Profit 수익내역 조회: 특정 계좌의 현재 코인 평가 자산 조회
    @ResponseBody
    @GetMapping("get/curCoinTotalProperty/{accountIdx}")
    @ApiOperation(value = "특정 포트폴리오 수익내역", notes = "Profit 수익내역 조회: 특정 포트폴리오의  특정 포트폴리오 전체 수익내역 조회")
    public BaseResponse<ProfitDto.GetCurCoinTotalPropertyRes> getCurCoinTotalPropertyByAccountIdx(@PathVariable("accountIdx") int accountIdx) {

        int userIdx = profitRepository.getUserIdxByAccountIdx(accountIdx); // 계좌 인덱스로 유저인덱스 조회
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            ProfitDto.GetCurCoinTotalPropertyReq getCurCoinTotalPropertyReq = new ProfitDto.GetCurCoinTotalPropertyReq(accountIdx);
            ProfitDto.GetCurCoinTotalPropertyRes getCurCoinTotalPropertyRes = profitService.getCurCoinTotalPropertyByAccountIdx(getCurCoinTotalPropertyReq);
            return new BaseResponse<>(getCurCoinTotalPropertyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * [GET]
     */
    // Profit 수익내역 조회: 특정 계좌의 총 손익급, 총 수익률 전체조회
    @ResponseBody
    @GetMapping("get/{accountIdx}")
    @ApiOperation(value = "특정 계좌의 수익(손익금,수익률)내역", notes = "Profit 수익내역 조회: 특정 계좌의 수익(손익금,수익률)내역 조회")
    public BaseResponse<List<ProfitDto.GetProfitRes>> getProfitByAccountIdx(@PathVariable("accountIdx") int accountIdx) {

        int userIdx = profitRepository.getUserIdxByAccountIdx(accountIdx); // 계좌 인덱스로 유저인덱스 조회
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            ProfitDto.GetProfitReq getProfitReq = new ProfitDto.GetProfitReq(accountIdx);
            List<ProfitDto.GetProfitRes> getProfitRes = profitService.getProfitByAccountIdx(getProfitReq);
            return new BaseResponse<>(getProfitRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * [GET]
     */
    // Profit 수익내역 일별 조회: 특정 계좌의 총 손익급, 총 수익률 전체조회
    @ResponseBody
    @GetMapping("get/daily/{accountIdx}")
    @ApiOperation(value = "특정 계좌의 일별 수익(손익금,수익률)내역", notes = "Profit 일별 수익내역 조회: 특정 계좌의 수익(손익금,수익률)내역 조회")
    public BaseResponse<List<ProfitDto.GetProfitRes>> getDailyProfitByAccountIdx(@PathVariable("accountIdx") int accountIdx) {

        int userIdx = profitRepository.getUserIdxByAccountIdx(accountIdx); // 계좌 인덱스로 유저인덱스 조회
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            ProfitDto.GetProfitReq getProfitReq = new ProfitDto.GetProfitReq(accountIdx);
            List<ProfitDto.GetProfitRes> getProfitRes = profitService.getDailyProfitByAccountIdx(getProfitReq);
            return new BaseResponse<>(getProfitRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * [GET]
     */
    // Profit 수익내역 주 별 조회: 특정 계좌의 총 손익급, 총 수익률 전체조회
    @ResponseBody
    @GetMapping("get/weekly/{accountIdx}")
    @ApiOperation(value = "특정 계좌의 주 별 수익(손익금,수익률)내역", notes = "Profit 주별 수익내역 조회: 특정 계좌의 수익(손익금,수익률)내역 조회")
    public BaseResponse<List<ProfitDto.GetProfitRes>> getWeeklyProfitByAccountIdx(@PathVariable("accountIdx") int accountIdx) {

        int userIdx = profitRepository.getUserIdxByAccountIdx(accountIdx); // 계좌 인덱스로 유저인덱스 조회
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            ProfitDto.GetProfitReq getProfitReq = new ProfitDto.GetProfitReq(accountIdx);
            List<ProfitDto.GetProfitRes> getProfitRes = profitService.getWeeklyProfitByAccountIdx(getProfitReq);
            return new BaseResponse<>(getProfitRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * [GET]
     */
    // Profit 수익내역 월 별 조회: 특정 계좌의 총 손익급, 총 수익률 전체조회
    @ResponseBody
    @GetMapping("get/monthly/{accountIdx}")
    @ApiOperation(value = "특정 계좌의 월 별 수익(손익금,수익률)내역", notes = "Profit 월 별 수익내역 조회: 특정 계좌의 수익(손익금,수익률)내역 조회")
    public BaseResponse<List<ProfitDto.GetProfitRes>> getMonthlyProfitByAccountIdx(@PathVariable("accountIdx") int accountIdx) {

        int userIdx = profitRepository.getUserIdxByAccountIdx(accountIdx); // 계좌 인덱스로 유저인덱스 조회
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            ProfitDto.GetProfitReq getProfitReq = new ProfitDto.GetProfitReq(accountIdx);
            List<ProfitDto.GetProfitRes> getProfitRes = profitService.getMonthlyProfitByAccountIdx(getProfitReq);
            return new BaseResponse<>(getProfitRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }




    /**
     * 수익내역 삭제 : status 수정
     * [PATCH] /profits/delete/:profitIdx
     */
    @PatchMapping("/delete/{profitIdx}")
    @ApiOperation(value = "수익내역", notes = "수익내역 삭제,status를 inactive로 수정")
    public BaseResponse<String> deleteProfit(@PathVariable("profitIdx") int profitIdx) {

        int userIdx = profitRepository.getUserIdxByProfitIdx(profitIdx); // 계좌 인덱스로 유저인덱스 조회
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }

            //**************************************************************************
            //같다면 수익내역 삭제
            ProfitDto.PatchStatusReq patchStatusReq = new ProfitDto.PatchStatusReq(profitIdx);
            profitService.deleteProfit(patchStatusReq);

            String result = "수익내역이 삭제되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }




}
