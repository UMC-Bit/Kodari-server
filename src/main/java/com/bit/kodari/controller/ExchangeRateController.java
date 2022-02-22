package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.ExchangeRateDto;
import com.bit.kodari.service.ExchangeRateService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/exchangeRates")
public class ExchangeRateController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService){
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * [GET]
     */
    // 환율 조회:
    @ResponseBody
    @GetMapping("/get")
    @ApiOperation(value = "환율",notes = "환율 조회")
    public BaseResponse<List<ExchangeRateDto.GetExchangeRateRes>> getExchageRate() throws IOException {
        try{
            List<ExchangeRateDto.GetExchangeRateRes> getExchangeRateRes = exchangeRateService.getExchageRate();
            return new BaseResponse<>(getExchangeRateRes);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
