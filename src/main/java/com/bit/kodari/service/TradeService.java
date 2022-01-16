package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.repository.trade.TradeRepository;
import com.bit.kodari.repository.user.UserRepository;
import com.bit.kodari.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TradeService {
    private final TradeRepository tradeRepository;
    private final JwtService jwtService; // JWT부분

    @Autowired //readme 참고
    public TradeService(TradeRepository tradeRepository, JwtService jwtService) {
        this.tradeRepository = tradeRepository;
        this.jwtService = jwtService; // JWT부분
    }

    // 거래내역 생성(POST)
    public TradeDto.PostTradeRes createTrade(TradeDto.PostTradeReq postTradeReq) throws BaseException{
        int portIdx = postTradeReq.getPortIdx();
        int coinIdx = postTradeReq.getCoinIdx();
        double price = postTradeReq.getPrice();
        double amount = postTradeReq.getAmount();
        double max = 100000000000L;
        double fee = postTradeReq.getFee();
        double maxFee = 100L;
        String category = postTradeReq.getCategory();
        String date = postTradeReq.getDate();

        // portIdx 범위 validation
        if(portIdx<=0){
            throw new BaseException(BaseResponseStatus.PORTIDX_RANGE_ERROR);
        }
        // coinIdx 범위 validation
        if(coinIdx<=0){
            throw new BaseException(BaseResponseStatus.COINIDX_RANGE_ERROR);
        }
        // 가격의 범위 validation
        if(price<=0 || price>max){
            throw new BaseException(BaseResponseStatus.PRICE_RANGE_ERROR);
        }
        // 갯수의 범위 validation
        if(amount<=0 || amount>max){
            throw new BaseException(BaseResponseStatus.AMOUNT_RANGE_ERROR);
        }
        // 수수료 범위 validation
        if(fee<0 || fee>maxFee){
            throw new BaseException(BaseResponseStatus.FEE_RANGE_ERROR);
        }
        // 매수/매도 null, 빈값 validation
        if(category == null || category.length()==0){
            throw new BaseException(BaseResponseStatus.EMPTY_CATEGORY);
        }
        // 거래시각 null, 빈값 validation
        if(date == null || date.length()==0){
            throw new BaseException(BaseResponseStatus.EMPTY_DATE);
        }

        // 입력값 검증되면 생성 요청
        try {
            // 거래내역 생성 요청
            TradeDto.PostTradeRes postTradeRes = tradeRepository.createTrade(postTradeReq);
            return postTradeRes;

//  *********** 해당 부분은 7주차 수업 후 주석해제하서 대체해서 사용해주세요! ***********
//            //jwt 발급.
//            int userIdx = postUserRes.getUserIdx();
//            //int userIdx = jwtService.getUserIdx();
//            String jwt = jwtService.createJwt(userIdx); // jwt 발급
//            return new UserDto.PostUserRes(userIdx,nickName,jwt); // jwt 담아서 서비스로 반환
//  *********************************************************************
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    // Trade 거래내역 조회: 특정 포트폴리오의  특정 코인의 전체 매수,매도 조회
    @Transactional
    public List<TradeDto.GetTradeRes> getTradeByPortIdxCoinIdx(TradeDto.GetTradeReq getTradeReq) throws BaseException {
        List<TradeDto.GetTradeRes> getTradeRes = tradeRepository.getTradeByPortIdxCoinIdx(getTradeReq);
        // 예외처리: 없는 닉네임일 경우
        if(getTradeRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_TRADES_NOT_EXISTS);
        }
        try {
            return getTradeRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    // 거래내역 수정 : 코인 가격 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updatePrice(TradeDto.PatchPriceReq patchPriceReq) throws BaseException{
        double price = patchPriceReq.getPrice();
        double max = 100000000000L;

        // 가격의 범위 validation
        if(price<=0 || price>max){
            throw new BaseException(BaseResponseStatus.PRICE_RANGE_ERROR);
        }

        int result = tradeRepository.updatePrice(patchPriceReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }


    // 거래내역 수정 : 코인 갯수 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateAmount(TradeDto.PatchAmountReq patchAmountReq) throws BaseException{
        double amount = patchAmountReq.getAmount();
        double max = 100000000000L;

        // 갯수의 범위 validation
        if(amount<=0 || amount>max){
            throw new BaseException(BaseResponseStatus.AMOUNT_RANGE_ERROR);
        }

        int result = tradeRepository.updateAmount(patchAmountReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }



    // 거래내역 수정 : 수수료 수정
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateFee(TradeDto.PatchFeeReq patchFeeReq) throws BaseException{
        double fee = patchFeeReq.getFee();
        double maxFee = 100L;

        // 수수료 범위 validation
        if(fee<0 || fee>maxFee){
            throw new BaseException(BaseResponseStatus.FEE_RANGE_ERROR);
        }

        int result = tradeRepository.updateFee(patchFeeReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }



    // 거래내역 수정 : 매수/매도 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateCategory(TradeDto.PatchCategoryReq patchCategoryReq) throws BaseException{
        String category = patchCategoryReq.getCategory();
        // 매수/매도 null, 빈값 validation
        if(category == null || category.length()==0){
            throw new BaseException(BaseResponseStatus.EMPTY_CATEGORY);
        }

        int result = tradeRepository.updateCategory(patchCategoryReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }



    // 거래내역 수정 : 메모 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateMemo(TradeDto.PatchMemoReq patchMemoReq) throws BaseException{

        int result = tradeRepository.updateMemo(patchMemoReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }



    // 거래내역 수정 : 거래시각 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateDate(TradeDto.PatchDateReq patchDateReq) throws BaseException{
        String date = patchDateReq.getDate();
        // 거래시각 null, 빈값 validation
        if(date == null || date.length()==0){
            throw new BaseException(BaseResponseStatus.EMPTY_DATE);
        }

        int result = tradeRepository.updateDate(patchDateReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }



    // 거래내역 삭제 : status 수정
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void deleteTrade(TradeDto.PatchStatusReq patchStatusReq) throws BaseException{

        int result = tradeRepository.deleteTrade(patchStatusReq);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }


}
