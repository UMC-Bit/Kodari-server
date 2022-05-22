package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.CoinDto;
import com.bit.kodari.dto.PostDto;
import com.bit.kodari.repository.coin.CoinRepository;
import com.bit.kodari.service.CoinService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/coins")
public class CoinController {

    @Autowired
    CoinService coinService;
    @Autowired
    CoinRepository coinRepository;
    @Autowired
    private final JwtService jwtService;

    public CoinController(CoinService coinService, JwtService jwtService) {
        this.coinService = coinService;
        this.jwtService = jwtService;
    }

    /*
        토론장 코인 조회
     */
    @GetMapping("") // (GET) 127.0.0.1:9000/coins
    @ApiOperation(value = "토론장 코인 목록 조회", notes = "토론장 코인 전체 조회함")
    public BaseResponse<List<CoinDto.GetCoinRes>> getCoins(@RequestParam(required = false) String coinName) {
        try {
            if (coinName == null) {
                List<CoinDto.GetCoinRes> getCoinsRes = coinService.getCoins();
                return new BaseResponse<>(getCoinsRes);
            }
            // query string인 userIdx이 있을 경우, 조건을 만족하는 상품정보들을 불러온다.
            List<CoinDto.GetCoinRes> getCoinsRes = coinService.getCoinsByCoinName(coinName);
            return new BaseResponse<>(getCoinsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //마켓별 코인 조회
    @GetMapping("/market")
    @ApiOperation(value = "market별 조회", notes = "market별 코인 조회함")
    public BaseResponse<List<CoinDto.GetCoinRes>> getCoinsByMarket (@RequestParam int marketIdx, @RequestParam(required = false) String coinName) {
        try {
            if (coinName == null) {
                List<CoinDto.GetCoinRes> getCoinRes = coinService.getCoinsByMarket(marketIdx);
                return new BaseResponse<>(getCoinRes);
            }
            List<CoinDto.GetCoinRes> getCoinsRes = coinService.getMarketCoinByCoinName(marketIdx, coinName);
            return new BaseResponse<>(getCoinsRes);

    } catch (BaseException exception) {
        return new BaseResponse<>((exception.getStatus()));
    }
}

}
