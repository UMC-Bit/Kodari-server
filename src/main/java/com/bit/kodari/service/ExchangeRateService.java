package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.ExchangeRateDto;
import com.bit.kodari.repository.ExchangeRate.ExchangeRateRepository;
import com.bit.kodari.utils.ExchangeRateApi;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class ExchangeRateService {
    @Autowired
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository){
        this.exchangeRateRepository = exchangeRateRepository;
    }

    //환율 조회:
    @Transactional
    public List<ExchangeRateDto.GetExchangeRateRes> getExchageRate() throws BaseException, IOException {
        List<ExchangeRateDto.GetExchangeRateRes> getExchangeRateRes = exchangeRateRepository.getExchageRate();

        // null 예외처리
        if(getExchangeRateRes.size()==0){
            throw new BaseException(BaseResponseStatus.GET_EXCHANGERATE_NOT_EXISTS);
        }

        try {
            return getExchangeRateRes;
        }catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 환율 업데이트: ExchangeRateApi를 이용해 현재 환율 시세로 업데이트
    @Transactional
    public void updateExchangePrice() throws BaseException,IOException{
        double exchangePrice=0;
        // 1.ExchangeRateApi에서 달러 시세 받아오기
        // 시세값 추출
        Response response = ExchangeRateApi.getExchangeRate(); // ExchangeRateApi에서 달러 시세 조회 요청
        String resultString = response.body().string(); // 주의: toString()은 안됌
        // 응답받은 json에서 필요한것으로 가공
        resultString= resultString.substring(62); // 응답받은 것에서 필요한 부분만 추출
        resultString="{ "+resultString;
        //System.out.println(resultString);

        JSONObject rjson = new JSONObject(resultString);// jsonarray를 추출하기 위해 json로 만듦
        JSONArray rjsonArray = rjson.getJSONArray("리스트"); // jsonarray 추출
        // jsonarray 배열 탐색
        for(int i=0;i<rjsonArray.length();i++){
            JSONObject obj = rjsonArray.getJSONObject(i); // 하나의 json객체 추출
            String money = obj.getString("통화명"); // 통화명 키값 추출
            exchangePrice = obj.getDouble("매매기준율"); // 매매기준율 값 추출
            //System.out.println(money+" "+exchangePrice);
            if(money.equals("미국 USD")){ break;} // 현재는 달러만 필요해서 찾으면 중단
        }

        int result = exchangeRateRepository.updateExchangePrice(exchangePrice);
        if(result == 0){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
