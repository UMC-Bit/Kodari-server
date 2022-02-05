package com.bit.kodari.utils;


import com.bit.kodari.config.BaseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UpbitApi {
    // 코인 현재가 정보 조회 api
    public static Response getCurrentPrice(String coinSymbol) throws BaseException {
        Response response=null;
        try{
            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url("https://api.upbit.com/v1/ticker?markets=KRW-"+coinSymbol)
                    .get()
                    .addHeader("Accept", "application/json")
                    .build();

            response = client.newCall(request).execute();

        }catch (IOException ioException){
            System.out.println("IOException occured.");
            System.exit(1);
        }

        return response;
    }


    // 원하는 날짜 기간 코인 종가 조회 api
    // Trade 과거 내역 삭제 시 그 날부터 오늘 어제 까지의 수익률 다시 계산하기 위해
    public static Response getPrevClosingPrice(String coinSymbol,String nowDate,String diffDay ) throws BaseException{
        Response response=null;
        String encodedDate = nowDate.substring(0,10); // 2022-1-30 까지 문자열 추출
        encodedDate+="%20"; // 공백을 url로 인코딩
        encodedDate+= nowDate.substring(11,13);
        encodedDate+= "%3A"; // ":" 를 url로 인코딩
        encodedDate+= nowDate.substring(14,16);
        encodedDate+= "%3A";
        encodedDate+= nowDate.substring(17);

        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.upbit.com/v1/candles/days?market=KRW-"+coinSymbol+"&to="+encodedDate+"&count="+diffDay)
                    .get()
                    .addHeader("Accept", "application/json")
                    .build();

            response = client.newCall(request).execute();
        }catch (IOException ioException){
            System.out.println("IOException occured.");
            System.exit(1);
        }
        return response;
    }

}
