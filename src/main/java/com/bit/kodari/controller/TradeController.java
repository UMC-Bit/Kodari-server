package com.bit.kodari.controller;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.PostDto;
import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.service.TradeService;
import com.bit.kodari.service.UserService;
import com.bit.kodari.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trades")
public class TradeController {

    @Autowired
    private final TradeService tradeService;
    @Autowired
    private final JwtService jwtService; // JWT부분

    public TradeController( TradeService tradeService, JwtService jwtService) {
        this.tradeService = tradeService;
        this.jwtService = jwtService; // JWT부분
    }



    /**
     * [POST]
     */
    //거래내역 생성 API
    @ResponseBody
    @PostMapping("/post")
    @ApiOperation(value = "거래내역", notes = "거래내역을 새로 등록함.")
    public BaseResponse createTrade(@RequestBody TradeDto.PostTradeReq postTradeReq) {
        //int userIdx = postAccountReq.getUserIdx();
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            // 거래내역 생성 요청
            TradeDto.PostTradeRes postTradeRes = tradeService.createTrade(postTradeReq);
            return new BaseResponse<>(postTradeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * [GET]
     */
    // Trade 거래내역 조회: 특정 포트폴리오의  특정 코인의 전체 매수,매도 조회
    @ResponseBody
    @GetMapping("get/{portIdx}/{coinIdx}")
    @ApiOperation(value = "특정 포트폴리오,특정 코인인덱스", notes = "Trade 거래내역 조회: 특정 포트폴리오의  특정 코인의 전체 매수,매도 조회")
    public BaseResponse<List<TradeDto.GetTradeRes>> getTradeByPortIdxCoinIdx(@PathVariable("portIdx") int portIdx, @PathVariable("coinIdx") int coinIdx) {

        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }

            TradeDto.GetTradeReq getTradeReq = new TradeDto.GetTradeReq(portIdx,coinIdx);
            List<TradeDto.GetTradeRes> getTradeRes = tradeService.getTradeByPortIdxCoinIdx(getTradeReq);
            return new BaseResponse<>(getTradeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 거래내역 수정 : 코인 가격 수정
     * [PATCH] /trades/update/price/:tradeIdx
     */
    @PatchMapping("/update/price/{tradeIdx}")
    @ApiOperation(value = "거래내역의 코인가격", notes = "코인가격 수정")
    public BaseResponse<String> updatePrice (@PathVariable("tradeIdx") int tradeIdx, @RequestBody TradeDto.PatchPriceReq patchPriceReq) {
        try {

//            // jwt 부분
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if (userIdx != userIdxByJwt) {
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }

            //**************************************************************************
            //같다면 코인 가격 수정
            TradeDto.PatchPriceReq patchPriceReq1 = new TradeDto.PatchPriceReq(tradeIdx,patchPriceReq.getPrice());
            tradeService.updatePrice(patchPriceReq1);

            String result = "거래내역의 코인가격이 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 거래내역 수정 : 코인 갯수 수정
     * [PATCH] /trades/update/amount/:tradeIdx
     */
    @PatchMapping("/update/amount/{tradeIdx}")
    @ApiOperation(value = "거래내역의 코인 갯수", notes = "코인 갯수 수정")
    public BaseResponse<String> updateAmount (@PathVariable("tradeIdx") int tradeIdx, @RequestBody TradeDto.PatchAmountReq patchAmountReq) {
        try {

//            // jwt 부분
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if (userIdx != userIdxByJwt) {
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }

            //**************************************************************************
            //같다면 코인 갯수 수정
            TradeDto.PatchAmountReq patchAmountReq1 = new TradeDto.PatchAmountReq(tradeIdx, patchAmountReq.getAmount());
            tradeService.updateAmount(patchAmountReq1);

            String result = "거래내역의 코인 갯수가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 거래내역 수정 : 수수료 수정
     * [PATCH] /trades/update/fee/:tradeIdx
     */
    @PatchMapping("/update/fee/{tradeIdx}")
    @ApiOperation(value = "거래내역의 수수료", notes = "수수료 수정")
    public BaseResponse<String> updateFee (@PathVariable("tradeIdx") int tradeIdx, @RequestBody TradeDto.PatchFeeReq patchFeeReq) {
        try {

//            // jwt 부분
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if (userIdx != userIdxByJwt) {
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }

            //**************************************************************************
            //같다면 코인 갯수 수정
            TradeDto.PatchFeeReq patchFeeReq1 = new TradeDto.PatchFeeReq(tradeIdx, patchFeeReq.getFee());
            tradeService.updateFee(patchFeeReq1);

            String result = "거래내역의 수수료가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 거래내역 수정 : 매수/매도 수정
     * [PATCH] /trades/update/category/:tradeIdx
     */
    @PatchMapping("/update/category/{tradeIdx}")
    @ApiOperation(value = "거래내역의 매수/매도", notes = "매수/매도 수정")
    public BaseResponse<String> updateCategory (@PathVariable("tradeIdx") int tradeIdx, @RequestBody TradeDto.PatchCategoryReq patchCategoryReq) {
        try {

//            // jwt 부분
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if (userIdx != userIdxByJwt) {
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }

            //**************************************************************************
            //같다면 코인 갯수 수정
            TradeDto.PatchCategoryReq patchCategoryReq1 = new TradeDto.PatchCategoryReq(tradeIdx, patchCategoryReq.getCategory());
            tradeService.updateCategory(patchCategoryReq1);

            String result = "거래내역의 매수/매도가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 거래내역 수정 : 메모 수정
     * [PATCH] /trades/update/memo/:tradeIdx
     */
    @PatchMapping("/update/memo/{tradeIdx}")
    @ApiOperation(value = "거래내역의 메모", notes = "메모 수정")
    public BaseResponse<String> updateMemo (@PathVariable("tradeIdx") int tradeIdx, @RequestBody TradeDto.PatchMemoReq patchMemoReq) {
        try {

//            // jwt 부분
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if (userIdx != userIdxByJwt) {
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }

            //**************************************************************************
            //같다면 코인 갯수 수정
            TradeDto.PatchMemoReq patchMemoReq1 = new TradeDto.PatchMemoReq(tradeIdx, patchMemoReq.getMemo());
            tradeService.updateMemo(patchMemoReq1);

            String result = "거래내역의 메모가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 거래내역 수정 : 거래시각 수정
     * [PATCH] /trades/update/date/:tradeIdx
     */
    @PatchMapping("/update/date/{tradeIdx}")
    @ApiOperation(value = "거래내역의 거래시각", notes = "거래시각 수정")
    public BaseResponse<String> updateDate (@PathVariable("tradeIdx") int tradeIdx, @RequestBody TradeDto.PatchDateReq patchDateReq) {
        try {

//            // jwt 부분
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if (userIdx != userIdxByJwt) {
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }

            //**************************************************************************
            //같다면 코인 갯수 수정
            TradeDto.PatchDateReq patchDateReq1 = new TradeDto.PatchDateReq(tradeIdx, patchDateReq.getDate());
            tradeService.updateDate(patchDateReq1);

            String result = "거래내역의 시각이 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 거래내역 삭제 : status 수정
     * [PATCH] /trades/delete/:tradeIdx
     */
    @PatchMapping("/delete/{tradeIdx}")
    @ApiOperation(value = "거래내역", notes = "거래내역 삭제,status를 inactive로 수정")
    public BaseResponse<String> deleteTrade (@PathVariable("tradeIdx") int tradeIdx) {
        try {

//            // jwt 부분
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if (userIdx != userIdxByJwt) {
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }

            //**************************************************************************
            //같다면 거래내역 삭제
            TradeDto.PatchStatusReq patchStatusReq = new TradeDto.PatchStatusReq(tradeIdx);
            tradeService.deleteTrade(patchStatusReq);

            String result = "거래내역이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



}
