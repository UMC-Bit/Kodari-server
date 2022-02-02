package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.repository.account.AccountRepository;
import com.bit.kodari.repository.usercoin.UserCoinRepository;
import com.bit.kodari.service.AccountService;
import com.bit.kodari.service.UserCoinService;
import com.bit.kodari.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/userCoin")
public class UserCoinController {
    /**
     - 소유코인 생성
     - userIdx, coinIdx, accountIdx
     - priceAvg(매수 평단가)
     - amount(코인 갯수)

     - 소유코인 수정
     - priceAvg(매수 평단가) 수정
     - amount(코인 갯수) 수정

     - 소유코인 삭제

     - 소유코인 조회
     - priceAvg, amount조회

     */

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserCoinService userCoinService;
    @Autowired
    private UserCoinRepository userCoinRepository;
    @Autowired
    private final JwtService jwtService;

    public UserCoinController(UserCoinService userCoinService, JwtService jwtService) {
        this.userCoinService = userCoinService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    /**
     * [POST]
     */
    //소유 코인 등록 API
    //Query String
    @ResponseBody
    @PostMapping("/post")
    public BaseResponse registerUserCoin(@RequestBody UserCoinDto.PostUserCoinReq postUserCoinReq) {
        int userIdx = postUserCoinReq.getUserIdx();
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            UserCoinDto.PostUserCoinRes userCoinRes = userCoinService.registerUserCoin(postUserCoinReq);
            return new BaseResponse<>(userCoinRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [GET]
     */

    // 특정 소유 코인 조회 API
    // Path-variable
    @ResponseBody
    @GetMapping("/user/{userCoinIdx}")
    public BaseResponse<List<UserCoinDto.GetUserCoinIdxRes>> getUserCoinIdx(@PathVariable("userCoinIdx") int userCoinIdx) {
        int userIdx = userCoinRepository.getUserIdxByUserCoinIdx(userCoinIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<UserCoinDto.GetUserCoinIdxRes> getUserCoinIdxRes = userCoinService.getUserCoinIdx(userCoinIdx);
            return new BaseResponse<>(getUserCoinIdxRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    // 소유 코인 조회 API
    // Path-variable
    @ResponseBody
    @GetMapping("/{portIdx}")
    public BaseResponse<List<UserCoinDto.GetUserCoinRes>> getUserCoin(@PathVariable("portIdx") int portIdx) {
        int userIdx = userCoinRepository.getUserIdxByPortIdx(portIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<UserCoinDto.GetUserCoinRes> getUserCoinRes = userCoinService.getUserCoin(portIdx);
            return new BaseResponse<>(getUserCoinRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * [PATCH]
     */
    //소유 코인 수정하기 - PATCH
    @ResponseBody
    @PatchMapping("/modify/{userCoinIdx}")
    public BaseResponse<String> modifyUserCoin(@PathVariable("userCoinIdx") int userCoinIdx, @RequestBody UserCoinDto.PatchUserCoinReq userCoin) {
        int userIdx = userCoinRepository.getUserIdxByUserCoinIdx(userCoinIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            UserCoinDto.PatchUserCoinReq patchUserCoinReq = new UserCoinDto.PatchUserCoinReq(userCoinIdx, userCoin.getPriceAvg(), userCoin.getAmount());
            userCoinService.updateUserCoin(patchUserCoinReq);

            String result = "코인이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //소유 코인 삭제하기
    @ResponseBody
    @PatchMapping("/del/{userCoinIdx}")
    public BaseResponse<String> deleteByUserCoinIdx(@PathVariable("userCoinIdx") int userCoinIdx) {

        int userIdx = userCoinRepository.getUserIdxByUserCoinIdx(userCoinIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            UserCoinDto.PatchUserCoinDelReq patchUserCoinDelReq = new UserCoinDto.PatchUserCoinDelReq(userCoinIdx);
            userCoinService.deleteByUserCoinIdx(patchUserCoinDelReq);

            String result = "소유 코인이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //소유 코인 전체 삭제하기
    // TODO 나중에 없애기
    @ResponseBody
    @PatchMapping("/delAll/{userIdx}")
    public BaseResponse<String> deleteByUserIdx(@PathVariable("userIdx") int userIdx) {

        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            UserCoinDto.PatchDelByUserIdxReq patchDelByUserIdxReq = new UserCoinDto.PatchDelByUserIdxReq(userIdx);
            userCoinService.deleteByUserIdx(patchDelByUserIdxReq);

            String result = "소유 코인이 전체 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //매수평단가 계산 update
    @ResponseBody
    @PatchMapping("/modify/BuySell/{userCoinIdx}")
    public BaseResponse<String> modifyPriceAvg(@PathVariable("userCoinIdx") int userCoinIdx) {
        int userIdx = userCoinRepository.getUserIdxByUserCoinIdx(userCoinIdx);
        //기존 코인 갯수
        double amount = userCoinRepository.getAmountByUserCoinIdx(userCoinIdx);
        //기존 매수평단가
        double priceAvg = userCoinRepository.getPriceAvg(userCoinIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            UserCoinDto.PatchBuySellReq patchBuySellReq = new UserCoinDto.PatchBuySellReq(userCoinIdx, priceAvg, amount);
            userCoinService.updatePriceAvg(patchBuySellReq);

            String result = "매수평단가가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
