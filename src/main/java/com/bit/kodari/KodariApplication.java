package com.bit.kodari;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.service.ExchangeRateService;
import com.bit.kodari.service.UserCoinService;
import com.bit.kodari.utils.BithumbWebSocketListener;
import com.bit.kodari.utils.ExchangeRateApi;
import com.bit.kodari.utils.UpbitApi;
import com.bit.kodari.utils.UpbitWebSocketListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication
@EnableScheduling // 일정 시간마다 자동으로 메소드 호출하는 스케줄러 사용가능하게 한다.
public class KodariApplication {

    public static void main(String[] args) throws ParseException, BaseException,IOException {
        SpringApplication.run(KodariApplication.class, args);

        // 메모리 사용량 출력
        long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("HEAP Size(M) : "+ heapSize / (1024*1024) + " MB");


        /*//환율 조회 api 테스트
        Response response = ExchangeRateApi.getExchangeRate();
        String resultString = response.body().string(); // 주의: toString()은 안됌

        //System.out.println(resultString);


        resultString= resultString.substring(62);
        //System.out.println(resultString);
        resultString="{ "+resultString;
        System.out.println(resultString);
        JSONObject rjson = new JSONObject(resultString);

        JSONArray rjsonArray = rjson.getJSONArray("리스트");
        for(int i=0;i<rjsonArray.length();i++){
            JSONObject obj = rjsonArray.getJSONObject(i);
            String money = obj.getString("통화명");
            double exchangePrice = obj.getDouble("매매기준율");
            System.out.println(money+" "+exchangePrice);
            if(money.equals("미국 USD")){ break;}
        }*/

//        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        //Date prev = transFormat.parse(prevTradeDate);
//        Date date=new Date(System.currentTimeMillis());
//        //Date date = new Date();
//        //date = transFormat.parse(date.toString());
//        System.out.println(transFormat.format(date));


//        String now = "2022-01-30 01:00:00";
//        String encodedDate = now.substring(0,10);
//        System.out.println(encodedDate);
//        encodedDate+="%20"; // 공백을 url로 인코딩
//        encodedDate+= now.substring(11,13);
//        encodedDate+= "%3A";
//        encodedDate+= now.substring(14,16);
//        encodedDate+= "%3A";
//        encodedDate+= now.substring(17);
//        System.out.println(encodedDate);
//
//        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date to = transFormat.parse(now);
//        Date date = new Date();
//        System.out.println(date.getTime()- to.getTime());
//        long diffDay = (date.getTime()- to.getTime()) / (24*60*60*1000);
//        System.out.println(diffDay);



        // 삭제하려는 거래내역의 과거 거래시각 조회
//        TradeDto.Trade getTradeRes = this.getTradeByTradeIdx(56);
//        String prevTradeDate = getTradeRes.getDate();
//        String prevTradeDate = "2022-02-06 14:41:51";
//        // 현재 시각 구하기
//        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date prev = transFormat.parse(prevTradeDate);
//        Date date = new Date();
//        long diffDay = (date.getTime()- prev.getTime()) / (24*60*60*1000)+1; // 현재-과거시간 으로 날짜 차이 구하기
//        //int dayCnt = date.toString().substring()
//        // 해당 코인 심볼 조회
//        List<UserCoinDto.GetUserCoinIdxRes> getUserCoinIdxRes = UserCoinService.getUserCoinIdx(userCoinIdx);
//        String symbol = getUserCoinIdxRes.get(0).getSymbol();
//        // 해당 코인의 과거 거래일시~어제까지의 수익내역 삭제
//        profitService.deleteProfitByUserCoinIdxDate(userCoinIdx,prevTradeDate);
//
//        // API 요청해서 해당 코인의 과거 거래일시~어제까지 일별 종가 시세 받아오기
//        Response response = UpbitApi.getPrevClosingPrice(symbol,date.toString(),Long.toString(diffDay));
//        String resultString = response.body().string();
//        // 업비트 api 응답이 에러코드일 Validation
//        if(resultString.charAt(0)=='{'){
//            //double trade_price = rjson.get("error"); // 코인 현재 시세 평단가
//            throw new BaseException(BaseResponseStatus.GET_UPBITAPI_ERROR);
//        }
        // 응답 받아온 json 문자열에서 jsonObject 생성
//        int len = resultString.length();
//        resultString = resultString.substring(1,len-1);// json앞 뒤 [] 문자 빼기
        // [] 포함해서 JSONArray 로 해보기
//        resultString = "{ \"dailyPrices\":"+resultString+"}";
//        JSONObject rjson = new JSONObject(resultString);
//        JSONArray rjsonArray = rjson.getJSONArray("dailyPrices");
//
//        for(int i=0;i<rjsonArray.length();i++){
//            JSONObject obj = rjsonArray.getJSONObject(i);
//            Double prevPrice = obj.getDouble("trade_price");
//            System.out.println(prevPrice);
//
//        }


        /*
        업비트 웹소켓 리스너 실행
         */
        UpbitWebSocketListener upbitWebSocket;
        HashSet<String> upbitCoinSymbolSet = new HashSet<>();
        upbitCoinSymbolSet.add("BTC");
        upbitWebSocket = new UpbitWebSocketListener(upbitCoinSymbolSet);
        upbitWebSocket.start(); // 업비트 웹 소켓 실행

        /*
        빗썸 웹소켓 리스너 실행
         */
        BithumbWebSocketListener bithumbWebSocket;
        HashSet<String> bithumbCoinSymbolSet = new HashSet<>();
        bithumbCoinSymbolSet.add("BTC");
        bithumbWebSocket = new BithumbWebSocketListener(bithumbCoinSymbolSet);
        bithumbWebSocket.start();// 웹 소켓 실행


    }

}
