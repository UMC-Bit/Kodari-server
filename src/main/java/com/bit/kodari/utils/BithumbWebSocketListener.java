package com.bit.kodari.utils;

import lombok.val;
import okhttp3.*;
import okio.ByteString;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

public class BithumbWebSocketListener extends WebSocketListener{

    private WebSocket webSocket;
    private HashSet coinSymbol;
    private String symbols=null;
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private String text;


    // 생성자 및 의존주입
    public BithumbWebSocketListener(HashSet<String> coinSymbol){
        this.coinSymbol = coinSymbol;
        this.symbols = getCodes(coinSymbol);
    }

    public final WebSocket getWebSocket() {
        return this.webSocket;
    }

    public final void setWebSocket( WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    // 소켓 통신 처음 메소드
    public void onOpen( WebSocket webSocket,  Response response) {
        text = "{\"type\":\"transaction\", \"symbols\":[" + this.symbols + "]}"; // 빗썸 웹소켓 데이터 요청 형식: {"type":"transaction데이터 종류", "symbols코인종류,화폐단위":["BTC_KRW" , "ETH_KRW"]}
        webSocket.send(text);
        this.webSocket = webSocket;
    }

    // 웹소켓으로 response받은 메시지 메소드
//    public void onMessage( WebSocket webSocket,  ByteString bytes) {
//        HashMap coinPriceMap = new HashMap();
//        String message = bytes.utf8();
//        System.out.println(message);
//        String symbol = (new JSONObject(message)).getString("symbol"); // 코인 이름
//        symbol = symbol.replace("_KRW","");
//        double price = (new JSONObject(message)).getDouble("contPrice"); // 현재 체결가격 추출
//        coinPriceMap.put(symbol, price);
//        String change = (new JSONObject(message)).getString("buySellGb"); // 체결종류(1:매도체결, 2:매수체결)
//        double changeNum = 0.0D;
//        if (change.equals("1")) { // 매도이면
//            changeNum = 1.0D;
//        } else if (change.equals("2")) { // 매수이면
//            changeNum = -1.0D;
//        }
//
//        coinPriceMap.put(symbol + "change", changeNum);
////        Log.d("Upbit_Socket", "Receiving bytes : " + bytes.utf8());
//        System.out.println("Bithumb_Socket"+"Receiving bytes : " + bytes.utf8());
//
//    }

    public void onMessage(WebSocket webSocket, String message ) {
        //System.out.println(message);

        HashMap coinPriceMap = new HashMap<String, Double>(); // d<코인심볼, 가격>
        //String message = bytes.utf8();
        try{
            JSONObject content = new JSONObject(message).getJSONObject("content");
            JSONArray list = content.getJSONArray("list");
            JSONObject response = list.getJSONObject(0);
            //        String test = (new JSONObject(message)).getString("type");
//        System.out.println(test);
            String symbol = response.getString("symbol"); // 코인 이름
            symbol = symbol.replace("_KRW",""); // _KRW 제거
            double price = response.getDouble("contPrice"); // 현재 체결가격 추출


            coinPriceMap.put(symbol, price);
            String change = response.getString("buySellGb"); // 체결종류(1:매도체결, 2:매수체결)
            double changeNum = 0.0D;
            if (change.equals("1")) { // 매도이면
                changeNum = 1.0D;
            } else if (change.equals("2")) { // 매수이면
                changeNum = -1.0D;
            }

            coinPriceMap.put(symbol + "change", changeNum);
//        Log.d("Upbit_Socket", "Receiving bytes : " + bytes.utf8());
            //System.out.println("Bithumb_Socket"+"Receiving bytes : " + bytes.utf8());
            System.out.println("Bithumb_Socket"+"Receiving message : " + message);
        }catch (JSONException jsonException){
            webSocket.send(text); // 응답 받은 후 다시 데이터 넘겨주기
            // 빗썸은 첫번째 , 두번째 응담이 json데이터가 아니고 다른 response이기 때문, 3번째 부터 데이터
        }



    }

    // 소켓 닫을 때 메소드
    public void onClosing( WebSocket webSocket, int code,  String reason) {
//        Log.d("Upbit_Socket", "Closing : " + code + " / " + reason);
        System.out.println("Bithumb_Socket"+ "Closing : " + code + " / " + reason);
        webSocket.close(1000, (String)null);
        webSocket.cancel();
    }

    // 소켓 전송 에러 시
    public void onFailure( WebSocket webSocket,  Throwable t,  Response response) {
//        Log.d("Upbit_Socket", "Error : " + t.getMessage());
        System.out.println("Bithumb_Socket"+"Error : " + t.getMessage());
        webSocket.cancel();
    }

    // 소켓 처음 실행 메소드
    public final void start() {
        Request request = new Request.Builder().url("wss://pubwss.bithumb.com/pub/ws").build();// 웹 소켓 url
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, (WebSocketListener)this);
        client.dispatcher().executorService().shutdown();
    }


    // 요청할 문자열 symbol-> codes 변환
    // 예) {"type":"transaction", "symbols":["BTC_KRW" , "ETH_KRW"]}에서  BTC => "BTC_KRW" 로 만듦
    //
    private final String getCodes(HashSet<String> coinSymbol) {
        //"BTC_KRW"
        StringBuilder sb = new StringBuilder();
        coinSymbol.forEach(it->{
            sb.append("\"");
            sb.append(it.toString());
            sb.append("_KRW");
            sb.append("\" , ");
        });
        //"BTC_KRW" , "ETH_KRW"
        sb.deleteCharAt(sb.length()-1); // "," 제거
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
