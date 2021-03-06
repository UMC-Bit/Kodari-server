package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.ProfitDto;
import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.repository.profit.ProfitRepository;
import com.bit.kodari.repository.trade.TradeRepository;
import com.bit.kodari.utils.BithumbApi;
import com.bit.kodari.utils.JwtService;
import com.bit.kodari.utils.UpbitApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

@Service
public class ProfitService {
    private final ProfitRepository profitRepository;
    private final JwtService jwtService; // JWT부분
    private final UserCoinService userCoinService;

    @Autowired //readme 참고
    public ProfitService(ProfitRepository profitRepository, JwtService jwtService, UserCoinService userCoinService) {
        this.profitRepository = profitRepository;
        this.jwtService = jwtService; // JWT부분
        this.userCoinService = userCoinService;
    }


    // 수익내역 생성(POST)
    @Transactional
    public ProfitDto.PostProfitRes createProfit(ProfitDto.PostProfitReq postProfitReq) throws BaseException, JSONException, IOException {
        // 요청 값 Validation
        int accountIdx = postProfitReq.getAccountIdx();
        // portIdx 범위 validation
        if(accountIdx<=0){
            throw new BaseException(BaseResponseStatus.ACCONTIDX_RANGE_ERROR);
        }
        // 계좌인덱스로 마켓인덱스 조회
        int marketIdx = profitRepository.getMarketIdxByAccountIdx(accountIdx);
        // marketIdx 범위 validation
        if(marketIdx<=0){
            throw new BaseException(BaseResponseStatus.MARKETIDX_RANGE_ERROR);
        }

        // 계좌의 처음 매수각격을 알기위해 유저코인에서 매수평단가, 코인갯수불러오기

        // 특정 계좌의 인덱스로 조회해서 총 손익금, 총 수익률 계산해서 Req 에 넣어서 생성요펑하기

        // 특정 계좌의 모든 코인 심볼 조회
        List<ProfitDto.GetCoinSymbolRes> getCoinSymbolRes = profitRepository.getSymbolByAccountIdx(accountIdx);
        // 코인 없는 경우 validation
        if (getCoinSymbolRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_SYMBOLS_NOT_EXISTS);
        }

        // 입력값 검증되면 생성 요청
        try {
            // 모든 코인 리스트 탐색하며 api로 현재 시세 불러와서 현재 코인 평가 자산 계산
            double sumCurProperty=0;
            double sumCoinPrice=0;
            for (int i = 0; i < getCoinSymbolRes.size(); i++) {
                String coinSymbol = getCoinSymbolRes.get(i).getSymbol(); // 코인 심볼 하나 추출
                // 업비트 거래소일때
                if(marketIdx == 1){
                    // 코인심볼로 업비트 api에서 현재 시세 조회
                    Response response = UpbitApi.getCurrentPrice(coinSymbol);
                    String resultString = response.body().string(); // json을 문자열로 추출
                    // 업비트 api 응답이 에러코드일 Validation
                    if(resultString.charAt(0)=='{'){
                        JSONObject rjson = new JSONObject(resultString); // json객체로 변환
                        //double trade_price = rjson.get("error"); // 코인 현재 시세 평단가
                        throw new BaseException(BaseResponseStatus.GET_UPBITAPI_ERROR);
                    }
                    // 업비트 API 응답 정살 이면
                    int len = resultString.length();
                    resultString = resultString.substring(1, len - 1); // json앞 뒤 [] 문자 빼기
                    JSONObject rjson = new JSONObject(resultString); // json객체로 변환
                    double trade_price = rjson.getDouble("trade_price"); // 코인 현재 시세 평단가


                    // 각 코인의 현재 평가 자산 = 현재시세 * 코인 갯수
                    double amount = getCoinSymbolRes.get(i).getAmount(); // 코인 갯수
                    double curProperty = trade_price*amount;
                    // 현재 총 자산에 더하기
                    sumCurProperty += curProperty;

                    // 내 코인의 총 매수금액
                    double priceAvg = getCoinSymbolRes.get(i).getPriceAvg();
                    sumCoinPrice += amount*priceAvg;
                }

                ///////////////////////////////////////////////////////////////////////
                // 빗썸 거래소일때
                if(marketIdx == 2){
                    // 코인심볼로 빗썸 api에서 현재 시세 조회
                    Response response = BithumbApi.getCurrentPrice(coinSymbol);
                    String resultString = response.body().string(); // json을 문자열로 추출
                    JSONObject rjson = new JSONObject(resultString); // json객체로 변환
                    // 빗썸 api 응답이 에러코드일 경우 Validation , 정상코드 = 0000
                    if(!rjson.getString("status").equals("0000")){
                        throw new BaseException(BaseResponseStatus.GET_BITHUMBAPI_ERROR);
                    }
                    // 빗썸 api 응답이 정상이면
                    JSONObject rjsonData = rjson.getJSONObject("data"); // data json객체 추출
                    double trade_price = rjsonData.getDouble("closing_price"); // data에서 코인 현재 시세 평단가


                    // 각 코인의 현재 평가 자산 = 현재시세 * 코인 갯수
                    double amount = getCoinSymbolRes.get(i).getAmount(); // 코인 갯수
                    double curProperty = trade_price*amount;
                    // 현재 총 자산에 더하기
                    sumCurProperty += curProperty;

                    // 내 코인의 총 매수금액
                    double priceAvg = getCoinSymbolRes.get(i).getPriceAvg();
                    sumCoinPrice += amount*priceAvg;
                }

            }

            // 총 매수 금액 = 총 자산 - 현금
            //double totalCoinProperty = getCoinSymbolRes.get(0).getTotalProperty() - getCoinSymbolRes.get(0).getProperty();

            // 총 손익금:  (현재 총 코인 자산) - 총 매수 금액
            double totalEarning = sumCurProperty - (sumCoinPrice);
            // 총 수익률: ( (현재 총 코인 자산) - (총 매수 금액))/ 총 매수금액 *100
            double totalProfitRate = (sumCurProperty - sumCoinPrice) / sumCoinPrice * 100;


            // 수익 생성 요청
            ProfitDto.PostProfitReq newPostProfitReq = new ProfitDto.PostProfitReq( accountIdx,totalProfitRate,totalEarning, postProfitReq.getDate());
            ProfitDto.PostProfitRes postProfitRes = profitRepository.createProfit(newPostProfitReq);
            return postProfitRes;

//  *********** 해당 부분은 7주차 수업 후 주석해제하서 대체해서 사용해주세요! ***********
//            //jwt 발급.
//            int userIdx = postUserRes.getUserIdx();
//            //int userIdx = jwtService.getUserIdx();
//            String jwt = jwtService.createJwt(userIdx); // jwt 발급
//            return new UserDto.PostUserRes(userIdx,nickName,jwt); // jwt 담아서 서비스로 반환
//  *********************************************************************
        } catch (BaseException exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(BaseResponseStatus.GET_UPBITAPI_ERROR);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }


    }





    // Profit 수익내역 조회: 특정 계좌의 현재 코인 평가 자산 조회
    @Transactional
    public ProfitDto.GetCurCoinTotalPropertyRes getCurCoinTotalPropertyByAccountIdx(ProfitDto.GetCurCoinTotalPropertyReq getCurCoinTotalPropertyReq) throws BaseException {
        // 특정 포트폴리오의 모든 코인 심볼 조회
        int accountIdx = getCurCoinTotalPropertyReq.getAccountIdx();
        // accountIdx 범위 validation
        if(accountIdx<=0){
            throw new BaseException(BaseResponseStatus.ACCONTIDX_RANGE_ERROR);
        }
        List<ProfitDto.GetCoinSymbolRes> getCoinSymbolRes = profitRepository.getSymbolByAccountIdx(accountIdx);
        // 수익내역 없는 경우 validation
        if (getCoinSymbolRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_SYMBOLS_NOT_EXISTS);
        }

        try {
            // 모든 코인 리스트 탐색하며 api로 현재 시세 불러와서 현재 코인 평가 자산 계산
            double sumCurProperty=0;
            for (int i = 0; i < getCoinSymbolRes.size(); i++) {
                String coinSymbol = getCoinSymbolRes.get(i).getSymbol(); // 코인 심볼 하나 추출
                // 코인심볼로 업비트 api에서 현재 시세 조회
                Response response = UpbitApi.getCurrentPrice(coinSymbol);
                String resultString = response.body().string(); // json을 문자열로 추출
                int len = resultString.length();
                resultString = resultString.substring(1, len - 1); // json앞 뒤 [] 문자 빼기

                JSONObject rjson = new JSONObject(resultString); // json객체로 변환
                double trade_price = rjson.getDouble("trade_price"); // 코인 현재 시세 평단가

                // 캌 코인의 현재 평가 자산 = 현재시세 * 코인 갯수
                double amount = getCoinSymbolRes.get(i).getAmount(); // 코인 갯수
                double curProperty = trade_price*amount;
                // 현재 총 자산에 더하기
                sumCurProperty += curProperty;
            }

            // 현재 총 코인자산 포함한 Res 생성 및 반환
            ProfitDto.GetCurCoinTotalPropertyRes getCurCoinTotalPropertyRes = new ProfitDto.GetCurCoinTotalPropertyRes(getCurCoinTotalPropertyReq.getAccountIdx(), sumCurProperty);
            return getCurCoinTotalPropertyRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



    // Profit 수익내역 조회: 특정 계좌의 전체 수익내역 조회
    @Transactional
    public List<ProfitDto.GetProfitRes> getProfitByAccountIdx(ProfitDto.GetProfitReq getProfitReq) throws BaseException {
        List<ProfitDto.GetProfitRes> getProfitRes = profitRepository.getProfitByAccountIdx(getProfitReq);
        // 수익내역 없는 경우 validation
        // 예외발생 말고 earining=0 으로 주기
        if(getProfitRes.size() == 0) {
            ProfitDto.GetProfitRes getProfitResEmpty = new ProfitDto.GetProfitRes(0,getProfitReq.getAccountIdx(),0,"0","inactive","0");
            getProfitRes.add(getProfitResEmpty);
            return getProfitRes;
            //throw new BaseException(BaseResponseStatus.GET_PROFITS_NOT_EXISTS);
        }

        // 총 손입금 지수형 E 없애기
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        for(int i=0;i<getProfitRes.size();i++){

            String earning = getProfitRes.get(i).getEarning();
            BigDecimal b = new BigDecimal(earning);
            //earning = b2.doubleValue();
            //earning = Double.parseDouble(format.format(earning));
            getProfitRes.get(i).setEarning( b.toString());
        }


        try {
          return getProfitRes;

        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



    // Profit 수익내역 일별 조회: 특정 계좌의 전체 수익내역 조회
    @Transactional
    public List<ProfitDto.GetProfitRes> getDailyProfitByAccountIdx(ProfitDto.GetProfitReq getProfitReq) throws BaseException {
        List<ProfitDto.GetProfitRes> getProfitRes = profitRepository.getDailyProfitByAccountIdx(getProfitReq);
        // 수익내역 없는 경우 validation
        // 예외발생 말고 earining=0 으로 주기
        if(getProfitRes.size() == 0) {
            ProfitDto.GetProfitRes getProfitResEmpty = new ProfitDto.GetProfitRes(0,getProfitReq.getAccountIdx(),0,"0","inactive","0");
            getProfitRes.add(getProfitResEmpty);
            return getProfitRes;
//            throw new BaseException(BaseResponseStatus.GET_PROFITS_NOT_EXISTS);
        }

        // 총 손입금 지수형 E 없애기
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        for(int i=0;i<getProfitRes.size();i++){

            String earning = getProfitRes.get(i).getEarning();
            BigDecimal b = new BigDecimal(earning);
            //earning = b2.doubleValue();
            //earning = Double.parseDouble(format.format(earning));
            getProfitRes.get(i).setEarning( b.toString());
        }


        try {
            return getProfitRes;

        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



    // Profit 수익내역 주 별 조회: 특정 계좌의 전체 수익내역 조회
    @Transactional
    public List<ProfitDto.GetProfitRes> getWeeklyProfitByAccountIdx(ProfitDto.GetProfitReq getProfitReq) throws BaseException {
        List<ProfitDto.GetProfitRes> getProfitRes = profitRepository.getWeeklyProfitByAccountIdx(getProfitReq);
        // 수익내역 없는 경우 validation
        // 예외발생 말고 earining=0 으로 주기
        if(getProfitRes.size() == 0) {
            ProfitDto.GetProfitRes getProfitResEmpty = new ProfitDto.GetProfitRes(0,getProfitReq.getAccountIdx(),0,"0","inactive","0");
            getProfitRes.add(getProfitResEmpty);
            return getProfitRes;
//            throw new BaseException(BaseResponseStatus.GET_PROFITS_NOT_EXISTS);
        }

        // 총 손입금 지수형 E 없애기
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        for(int i=0;i<getProfitRes.size();i++){

            String earning = getProfitRes.get(i).getEarning();
            BigDecimal b = new BigDecimal(earning);
            //earning = b2.doubleValue();
            //earning = Double.parseDouble(format.format(earning));
            getProfitRes.get(i).setEarning( b.toString());
        }


        try {
            return getProfitRes;

        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



    // Profit 수익내역 월 별 조회: 특정 계좌의 전체 수익내역 조회
    @Transactional
    public List<ProfitDto.GetProfitRes> getMonthlyProfitByAccountIdx(ProfitDto.GetProfitReq getProfitReq) throws BaseException {
        List<ProfitDto.GetProfitRes> getProfitRes = profitRepository.getMonthlyProfitByAccountIdx(getProfitReq);
        // 수익내역 없는 경우 validation
        // 예외발생 말고 earining=0 으로 주기
        if(getProfitRes.size() == 0) {
            ProfitDto.GetProfitRes getProfitResEmpty = new ProfitDto.GetProfitRes(0,getProfitReq.getAccountIdx(),0,"0","inactive","0");
            getProfitRes.add(getProfitResEmpty);
            return getProfitRes;
//            throw new BaseException(BaseResponseStatus.GET_PROFITS_NOT_EXISTS);
        }

        // 총 손입금 지수형 E 없애기
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        for(int i=0;i<getProfitRes.size();i++){

            String earning = getProfitRes.get(i).getEarning();
            BigDecimal b = new BigDecimal(earning);
            //earning = b2.doubleValue();
            //earning = Double.parseDouble(format.format(earning));
            getProfitRes.get(i).setEarning( b.toString());
        }


        try {
            return getProfitRes;

        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    // 수익내역 조회: 수익내역의 accountIdx 전체조회
    @Transactional
    public List<Integer> getAllAccountIdx() throws BaseException {
        List<Integer> getAllAccountIdxRes = profitRepository.getAllAccountIdx();
        // 거래내역 없는 경우 validation
        if(getAllAccountIdxRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_TRADES_NOT_EXISTS);
        }
        try {
            return getAllAccountIdxRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



    // 수익내역 삭제 : status 변경
    @Transactional
    public void deleteProfit(ProfitDto.PatchStatusReq patchStatusReq) throws BaseException{
        // 이미 삭제된 수익내역 삭제방지 validation
        String status = profitRepository.getStatusByProfitIdx(patchStatusReq.getProfitIdx());
        if(status.equals("inactive")){
            throw new BaseException(BaseResponseStatus.ALREADY_DELETED_PROFIT); //
        }
        // 삭제 할 수 있으면
        int result = profitRepository.deleteProfit(patchStatusReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }


    // 수익내역 삭제 : 전체삭제
    @Transactional
    public void deleteAllProfitByUserIdx(int userIdx) throws BaseException{

        // 수익내역 삭제 요청
        int result = profitRepository.deleteAllProfitByUserIdx(userIdx);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }


    // Trade연동 수익내역 삭제
    @Transactional
    public void deleteProfitByUserCoinIdxDate(int userCoinIdx,String date) throws BaseException{


        // 원하는 시점 이후 수익내역 삭제 요청
        int result = profitRepository.deleteProfitByUserCoinIdx(userCoinIdx,date);
        if(result == 0){// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }



}
