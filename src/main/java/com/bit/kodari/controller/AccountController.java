package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.repository.account.AccountRepository;
import com.bit.kodari.service.AccountService;
import com.bit.kodari.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/account")
public class AccountController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private final JwtService jwtService;

    public AccountController(AccountService accountService, JwtService jwtService) {
        this.accountService = accountService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    /**
     * [POST]
     */
    //유저 계좌 등록 API
    //Query String
    @ResponseBody
    @PostMapping("/post")
    public BaseResponse registerAccount(@RequestBody AccountDto.PostAccountReq postAccountReq) {
        int userIdx = postAccountReq.getUserIdx();
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            AccountDto.PostAccountRes postAccountRes = accountService.registerAccount(postAccountReq);
            return new BaseResponse<>(postAccountRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [GET]
     */

    // 유저 계좌 조회
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<List<AccountDto.GetAccountRes>> getAccountByUserIdx(@PathVariable("userIdx") int userIdx) {

        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<AccountDto.GetAccountRes> getAccountRes = accountService.getAccountByUserIdx(userIdx);
            return new BaseResponse<>(getAccountRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //현금 자산 조회
    //Query String
    @ResponseBody
    @GetMapping("/property/{accountIdx}")
    public BaseResponse<List<AccountDto.GetPropertyRes>> getProperty(@PathVariable("accountIdx") int accountIdx) {
        int userIdx = accountRepository.getUserIdxByAccountIdx(accountIdx);
        try {
            // 해당 accountIdx를 만족하는 계좌의 현금 자산 정보를 불러온다.
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<AccountDto.GetPropertyRes> getPropertyRes = accountService.getProperty(accountIdx);
            return new BaseResponse<>(getPropertyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * [PATCH]
     */
    //계좌 이름 수정하기 - PATCH
    @ResponseBody
    @PatchMapping("/modifyAccountName/{accountIdx}")
    public BaseResponse<String> modifyAccountName(@PathVariable("accountIdx") int accountIdx, @RequestBody AccountDto.PatchAccountNameReq account) {
        int userIdx = accountRepository.getUserIdxByAccountIdx(accountIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
                AccountDto.PatchAccountNameReq patchAccountNameReq = new AccountDto.PatchAccountNameReq(account.getAccountName(), accountIdx);
                accountService.updateAccountName(patchAccountNameReq);

                String result = "계좌 이름이 수정되었습니다.";
                return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //현금 자산 수정하기
    @ResponseBody
    @PatchMapping("/modifyProperty/{accountIdx}")
    public BaseResponse<String> modifyProperty(@PathVariable("accountIdx") int accountIdx, @RequestBody AccountDto.PatchPropertyReq account) {
        int userIdx = accountRepository.getUserIdxByAccountIdx(accountIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            AccountDto.PatchPropertyReq patchPropertyReq = new AccountDto.PatchPropertyReq(accountIdx, account.getProperty());
            accountService.updateProperty(patchPropertyReq);

            String result = "현금 자산 수정이 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // Trade - 현금 자산 수정하기
    @ResponseBody
    @PatchMapping("/modify/property/{tradeIdx}")
    public BaseResponse<String> modifyTradeProperty(@PathVariable("tradeIdx") int tradeIdx) {
        int portIdx = accountRepository.getPortIdx(tradeIdx);
        int accountIdx = accountRepository.getAccountIdx(portIdx);
        //int userIdx = accountRepository.getUserIdxByPort(accountIdx);
        try {
            //jwt에서 idx 추출.
            //int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            //if(userIdx != userIdxByJwt){
                //return new BaseResponse<>(INVALID_USER_JWT);
            //}
            AccountDto.PatchTradePropertyReq patchTradePropertyReq = new AccountDto.PatchTradePropertyReq(tradeIdx, accountIdx);
            accountService.updateTradeProperty(patchTradePropertyReq);

            String result = "현금 자산이 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //계좌 삭제하기 - PATCH
    @ResponseBody
    @PatchMapping("/delAccount/{accountIdx}")
    public BaseResponse<String> deleteByName(@PathVariable("accountIdx") int accountIdx) {
        int userIdx = accountRepository.getUserIdxByAccountIdx(accountIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            AccountDto.PatchAccountDelReq patchAccountDelReq = new AccountDto.PatchAccountDelReq(accountIdx);
            accountService.deleteByName(patchAccountDelReq);

            String result = "계좌가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
