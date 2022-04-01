package com.bit.kodari.utils;

import com.fasterxml.jackson.databind.JsonNode;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import lombok.Data;
import okhttp3.*;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class UpbitWebSocketListener extends WebSocketListener {

    private WebSocket webSocket;
    private HashSet coinSymbol;
    private String symbols=null;
    private static final int NORMAL_CLOSURE_STATUS = 1000;


    public UpbitWebSocketListener(HashSet<String> coinSymbol){
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
        String text = "[{\"ticket\":\"kodari\"},{\"type\":\"ticker\",\"codes\":[" + this.symbols + "]}]";
        webSocket.send(text);
        this.webSocket = webSocket;
    }

    // 웹소켓으로 response받은 메시지 메소드
    public void onMessage( WebSocket webSocket,  ByteString bytes) {
        HashMap coinPriceMap = new HashMap();
        String message = bytes.utf8();
        String symbol = (new JSONObject(message)).getString("code");
        symbol = symbol.replace("KRW-","");
        double price = (new JSONObject(message)).getDouble("trade_price");
        coinPriceMap.put(symbol, price);
        String change = (new JSONObject(message)).getString("ask_bid");
        double changeNum = 0.0D;
        if (change.equals("ASK")) {
            changeNum = 1.0D;
        } else if (change.equals("BID")) {
            changeNum = -1.0D;
        }

        coinPriceMap.put(symbol + "change", changeNum);
//        Log.d("Upbit_Socket", "Receiving bytes : " + bytes.utf8());
        System.out.println("Upbit_Socket"+"Receiving bytes : " + bytes.utf8());

    }

    // 소켓 닫을 때 메소드
    public void onClosing( WebSocket webSocket, int code,  String reason) {
//        Log.d("Upbit_Socket", "Closing : " + code + " / " + reason);
        System.out.println("Upbit_Socket"+ "Closing : " + code + " / " + reason);
        webSocket.close(1000, (String)null);
        webSocket.cancel();
    }

    // 소켓 전송 에러 시
    public void onFailure( WebSocket webSocket,  Throwable t,  Response response) {
//        Log.d("Upbit_Socket", "Error : " + t.getMessage());
        System.out.println("Upbit_Socket"+"Error : " + t.getMessage());
        webSocket.cancel();
    }

    // 소켓 처음 실행 메소드
    public final void start() {
        Request request = new Request.Builder().url("wss://api.upbit.com/websocket/v1").build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, (WebSocketListener)this);
        client.dispatcher().executorService().shutdown();
    }

    // 요청할 문자열 symbol-> codes 변환
    private final String getCodes(HashSet<String> coinSymbol) {
        //"KRW-BTC","BTC-BCH"
        StringBuilder sb = new StringBuilder();
        coinSymbol.forEach(it->{
            sb.append("\"");
            sb.append("KRW-");
            sb.append(it.toString());
            sb.append("\",");
        });

        sb.deleteCharAt(sb.length()-1); // "," 제거
        return sb.toString();
    }
}



