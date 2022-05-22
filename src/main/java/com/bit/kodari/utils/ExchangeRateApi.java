package com.bit.kodari.utils;

import com.bit.kodari.config.BaseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ExchangeRateApi {
    // 현재 화폐 환율 조회 api
    public static Response getExchangeRate () throws BaseException{
        Response response=null;
        try{
            OkHttpClient client = new OkHttpClient(); // http 요청 객체

            // 요청 내용을 담는 객체
            Request request = new Request.Builder()
                    .url("http://fx.kebhana.com/FER1101M.web") // url
                    .get() // 요청 메소드
                    .addHeader("accept","application/json") // 헤더
                    .build();

            response = client.newCall(request).execute(); // 요청 실행

        }catch (IOException ioException){
            System.out.println("IOException occured.");
            System.exit(1);
        }

        return response;
    }
}
