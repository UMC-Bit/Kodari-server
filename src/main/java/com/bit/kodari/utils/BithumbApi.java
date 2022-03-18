package com.bit.kodari.utils;

import com.bit.kodari.config.BaseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BithumbApi {

    // 코인 현재가 정보 조회 api
    public static Response getCurrentPrice(String coinSymbol) throws BaseException {
        Response response=null;
        String urlStr = "https://api.bithumb.com/public/ticker/"+coinSymbol+"_KRW"; // 빗썸 현재 시세 api 링크 + 코인종류 + 원화(KRW)

        try{
            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url(urlStr) // request 요청 링크
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
