package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.PortfolioDto;
import com.bit.kodari.repository.account.AccountRepository;
import com.bit.kodari.repository.portfolio.PortfolioRepository;
import com.bit.kodari.service.AccountService;
import com.bit.kodari.service.PortfolioService;
import com.bit.kodari.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PortfolioService portfolioService;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private final JwtService jwtService;

    public PortfolioController(PortfolioService portfolioService, JwtService jwtService) {
        this.portfolioService = portfolioService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    /**
     * [POST]
     */
    //포트폴리오 등록 API
    //Query String
    @ResponseBody
    @PostMapping("/post")
    public BaseResponse registerPortfolio(@RequestBody PortfolioDto.PostPortfolioReq postPortfolioReq) {
        //int userIdx = postPortfolioReq.getUserIdx();
        try {
            //jwt에서 idx 추출.
            //int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            //if(userIdx != userIdxByJwt){
                //return new BaseResponse<>(INVALID_USER_JWT);
            //}
            PortfolioDto.PostPortfolioRes postPortfolioRes = portfolioService.registerPortfolio(postPortfolioReq);
            return new BaseResponse<>(postPortfolioRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [GET]
     */
    //포트폴리오 조회하기
    //계좌 이름, 현금자산, 대표코인, 소유코인 등등 모든 정보들
    //Query String
    @ResponseBody
    @GetMapping("/all/{portIdx}")
    public BaseResponse<PortfolioDto.GetPortfolioRes> getPortfolio(@PathVariable("portIdx") int portIdx) {

        try {
            // 해당 accountIdx를 만족하는 계좌의 현금 자산 정보를 불러온다.
            //jwt에서 idx 추출.
            //int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            //if(userIdx != userIdxByJwt){
                //return new BaseResponse<>(INVALID_USER_JWT);
            //}
            PortfolioDto.GetPortfolioRes getPortfolioRes = portfolioService.getPortfolio(portIdx);
            return new BaseResponse<>(getPortfolioRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * [PATCH]
     */
    //포트폴리오 삭제하기 - PATCH
    @ResponseBody
    @PatchMapping("/delPortfolio/{portIdx}")
    public BaseResponse<String> deleteByPortIdx(@PathVariable("portIdx") int portIdx) {
        //int userIdx = portfolioRepository.getUserIdxByPortIdx(portIdx);
        try {
            //jwt에서 idx 추출.
            //int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            //if(userIdx != userIdxByJwt){
                //return new BaseResponse<>(INVALID_USER_JWT);
            //}
            PortfolioDto.PatchPortfolioDelReq patchPortfolioDelReq = new PortfolioDto.PatchPortfolioDelReq(portIdx);
            portfolioService.deleteByPortIdx(patchPortfolioDelReq);

            String result = "포트폴리오가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
