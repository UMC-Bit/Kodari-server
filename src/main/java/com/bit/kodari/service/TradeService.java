package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.ProfitDto;
import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.repository.account.AccountRepository;
import com.bit.kodari.repository.coin.CoinRepository;
import com.bit.kodari.repository.portfolio.PortfolioRepository;
import com.bit.kodari.repository.profit.ProfitRepository;
import com.bit.kodari.repository.trade.TradeRepository;
import com.bit.kodari.repository.user.UserRepository;
import com.bit.kodari.repository.usercoin.UserCoinRepository;
import com.bit.kodari.utils.JwtService;
import com.bit.kodari.utils.UpbitApi;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.MODIFY_FAIL_PRICE_AVG;

@Service
public class TradeService {
    private final TradeRepository tradeRepository;
    private final JwtService jwtService; // JWT부분
    private final AccountService accountService;
    private final PortfolioRepository portfolioRepository;
    private final AccountRepository accountRepository;
    private final UserCoinRepository userCoinRepository;
    private final ProfitService profitService;
    private final UserCoinService userCoinService;
    private final ProfitRepository profitRepository;



    @Autowired //readme 참고
    public TradeService(TradeRepository tradeRepository, JwtService jwtService, AccountService accountService, PortfolioRepository portfolioRepository, AccountRepository accountRepository
    ,UserCoinRepository userCoinRepository ,ProfitService profitService, UserCoinService userCoinService, ProfitRepository profitRepository) {
        this.tradeRepository = tradeRepository;
        this.jwtService = jwtService; // JWT부분
        this.accountService = accountService;
        this.portfolioRepository = portfolioRepository;
        this.accountRepository = accountRepository;
        this.userCoinRepository = userCoinRepository;
        this.profitService = profitService;
        this.userCoinService = userCoinService;
        this.profitRepository = profitRepository;
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
        int accountIdx = portfolioRepository.getAccountIdx(portIdx);
        int userIdx = portfolioRepository.getAccountUserIdx(accountIdx);
        List<TradeDto.GetUserCoinInfoRes> getUserCoinInfoRes = tradeRepository.getCoinIdxRes(userIdx, accountIdx);

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

        // 매수를 진행할 때 사용자의 계좌의 돈이 부족한 경우 Validation
        if(category.equals("buy")){
            // 계좌의 현금 - (매수하는 코인 평단가 * 갯수) - 수수료 < 0
            // Trade에서 portIdx를 통해 Portfolio(accountIdx) 구하기
            // Account에서 property 구하기
            double cashProperty = accountRepository.getPropertyByAccount(accountIdx);
            if((cashProperty - price*amount - price*amount*fee)<0){
                throw new BaseException(BaseResponseStatus.LACK_OF_PROPERTY);
            }
        }

        // 매도를 진행할 때 매도량이 사용자의 소유코인 개수보다 더 많을경우 Validation
        else if(category.equals("sell")){
            // coinIdx, accountIdx로 UserCoin.amount 조회
            //  소유코인갯수 - 매도량  < 0 면 소유코인 갯수 부족 에러
            double userCoinAmount = tradeRepository.getUserCoinAmountByAccountIdxCoinIdx(accountIdx,coinIdx);
            if((userCoinAmount - amount)<0){
                throw new BaseException(BaseResponseStatus.LACK_OF_AMOUNT);
            }
        }

        // userIdx,cionIdx,accountIdx로 유저 코인 있는지 검사 Validation
        int sum=0;
        int idx = 0;
        for(int i=0; i < getUserCoinInfoRes.size(); i++){
            if(getUserCoinInfoRes.get(i).getCoinIdx() == coinIdx){
                idx = i;
                sum++;
                break;
            }
        }



        // 입력값 검증되면 생성 요청
        try {
            // 거래내역 생성 요청
            TradeDto.PostTradeRes postTradeRes = tradeRepository.createTrade(postTradeReq);

            // 매수,매도 거래내역 등록 완료 시 Account 보유현금, 총 자산 자동 업데이트
            accountService.updateTradeProperty(postTradeRes.getTradeIdx());
            // 소유코인이 존재하지 않을때
            if(sum == 0){
                if(category.equals("buy")){
                    // 소유코인을 새로 생성하고
                    UserCoinDto.PostUserCoinReq postUserCoinReq = new UserCoinDto.PostUserCoinReq(userIdx, coinIdx, accountIdx, price, amount);
                    userCoinService.registerUserCoin(postUserCoinReq);
                }
                else if(category.equals("sell")){
                    // 소유코인이 없을때 매도는 오류처리
                   throw new BaseException(BaseResponseStatus.NO_USER_COIN); //4057
                }
            }
            else if(sum != 0){
                //소유코인이 있을때
                //기존의 해당 userCoinIdx 조회
                int userCoinIdx = getUserCoinInfoRes.get(idx).getUserCoinIdx();
                //기존 코인 갯수
                double existAmount = userCoinRepository.getAmountByUserCoinIdx(userCoinIdx);
                //기존 매수평단가
                double priceAvg = userCoinRepository.getPriceAvg(userCoinIdx);
                // 매수평단가, amount 수정
                UserCoinDto.PatchBuySellReq patchBuySellReq = new UserCoinDto.PatchBuySellReq(userCoinIdx, priceAvg, existAmount);
                userCoinService.updatePriceAvg(patchBuySellReq);
            }


            // 수익내역 생성 요청
            ProfitDto.PostProfitReq postProfitReq = new ProfitDto.PostProfitReq();
            postProfitReq.setAccountIdx(accountIdx);// 수익내역 요청 dto 의 accountIdx 저장
            postProfitReq.setDate(date);
            profitService.createProfit(postProfitReq);

            return postTradeRes;// 거래내역 반환

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
        // 거래내역 없는 경우 validation
        if(getTradeRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_TRADES_NOT_EXISTS);
        }
        try {
            return getTradeRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



    // Trade 거래내역 조회: tradeIdx로 특정 거래내역 조회
    @Transactional
    public TradeDto.Trade getTradeByTradeIdx(int tradeIdx) throws BaseException {
        TradeDto.Trade getTradeRes = tradeRepository.getTradeByTradeIdx(tradeIdx);
        // 거래내역 없는 경우 validation
        if(getTradeRes == null) {
            throw new BaseException(BaseResponseStatus.GET_TRADES_NOT_EXISTS);
        }
        try {
            return getTradeRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    @Transactional
    public List<TradeDto.GetTradeInfoRes> getTradeInfo(int tradeIdx) throws BaseException{
        List<TradeDto.GetTradeInfoRes> getTradeInfoRes = tradeRepository.getTradeInfo(tradeIdx);
        // 거래내역 없는 경우 validation
        if(getTradeInfoRes.size()==0){
            throw new BaseException(BaseResponseStatus.GET_TRADES_NOT_EXISTS);
        }
        try {
            return getTradeInfoRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 거래내역 수정 : 코인 가격 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updatePrice(TradeDto.PatchPriceReq patchPriceReq) throws BaseException{
        double newPrice = patchPriceReq.getPrice(); //새로 수정할 가격
        double max = 100000000000L;
        int userCoinIdx = tradeRepository.getUserCoinIdxByTradeIdx(patchPriceReq.getTradeIdx());
        List<TradeDto.GetTradeInfoRes> getTradeInfoRes = tradeRepository.getTradeInfo(patchPriceReq.getTradeIdx());
        double price = getTradeInfoRes.get(0).getPrice(); // 코인 원래 가격
        double amount = getTradeInfoRes.get(0).getAmount(); // 코인 원래 갯수
        double fee = getTradeInfoRes.get(0).getFee(); // 코인 수수료
        String category = getTradeInfoRes.get(0).getCategory(); //매수 or 매도 : “buy”, “sell”
        double property = getTradeInfoRes.get(0).getProperty(); //원래 현금 자산
        //double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산
        double priceAvg = getTradeInfoRes.get(0).getPriceAvg(); //매수 평단가
        double uc_amount = getTradeInfoRes.get(0).getUc_amount(); //소유 코인 테이블의 코인 갯수

        double newProperty = 0; // 업데이트 해줄 새로운 현금 자산

        // 가격의 범위 validation
        if(newPrice<=0 || newPrice>max){
            throw new BaseException(BaseResponseStatus.PRICE_RANGE_ERROR);
        }

        // 거래 내역 수정시 - 매수평단가, amount
        // 거래 내역 수정시 - 현금자산, 총자산
        if(price<=0 || price>max){
            throw new BaseException(BaseResponseStatus.PRICE_RANGE_ERROR);
        }else if(category.equals("buy")){
            // "buy"매수라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 더해준 후 새로운 것 빼주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기.

            newProperty = property + ((price * amount) + (price * amount * fee)) - ((newPrice * amount) - (newPrice * amount * fee)); // 새로운 현금 자산 계산
            //totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
            priceAvg = (priceAvg * uc_amount - price * amount + newPrice * amount) / uc_amount; //새로운 매수평단가
        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            newProperty = property - (price * amount) - (price * amount * fee) + (newPrice * amount) + (newPrice * amount * fee); // 새로운 현금 자산 계산
            //totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
        }

        int result = tradeRepository.updatePrice(patchPriceReq);
        if(category.equals("buy")){
            int propertyResult = tradeRepository.modifyProperty(newProperty, patchPriceReq.getTradeIdx());
            int priceAvgResult = tradeRepository.updatePriceAvg(userCoinIdx, priceAvg);
        }else if(category.equals("sell")){
            int propertyResult = tradeRepository.modifyProperty(newProperty, patchPriceReq.getTradeIdx());
        }
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }


    // 거래내역 수정 : 코인 갯수 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateAmount(TradeDto.PatchAmountReq patchAmountReq) throws BaseException{
        double amount = patchAmountReq.getAmount();
        double max = 100000000000L;
        int userCoinIdx = tradeRepository.getUserCoinIdxByTradeIdx(patchAmountReq.getTradeIdx());
        List<TradeDto.GetTradeInfoRes> getTradeInfoRes = tradeRepository.getTradeInfo(patchAmountReq.getTradeIdx());
        double price = getTradeInfoRes.get(0).getPrice(); // 코인 원래 가격
        double existAmount = getTradeInfoRes.get(0).getAmount(); // 코인 원래 갯수
        double fee = getTradeInfoRes.get(0).getFee(); // 코인 수수료
        String category = getTradeInfoRes.get(0).getCategory(); //매수 or 매도 : “buy”, “sell”
        double property = getTradeInfoRes.get(0).getProperty(); //원래 현금 자산
        //double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산
        double priceAvg = getTradeInfoRes.get(0).getPriceAvg(); //매수 평단가
        double uc_amount = getTradeInfoRes.get(0).getUc_amount(); //소유 코인 테이블의 코인 갯수

        double newProperty = 0; // 업데이트 해줄 새로운 현금 자산
        double sumCoinAmount = 0; //새로운 코인 전체 갯수

        // 갯수의 범위 validation
        if(amount<=0 || amount>max){
            throw new BaseException(BaseResponseStatus.AMOUNT_RANGE_ERROR);
        }

        // 거래 내역 수정시 - 매수평단가, amount
        // 거래 내역 수정시 - 현금자산, 총자산
        if(existAmount<=0 || existAmount>max){
            throw new BaseException(BaseResponseStatus.AMOUNT_RANGE_ERROR);
        } else if(category.equals("buy")){
            // "buy"매수라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 더해준 후 새로운 것 빼주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기.
            sumCoinAmount = uc_amount - existAmount + amount;
            if(sumCoinAmount < 0){
                throw new BaseException(BaseResponseStatus.AMOUNT_RANGE_ERROR);
            }
            else{
                newProperty = property + (price * existAmount) + (price * existAmount * fee) - (price * amount) - (price * amount * fee); // 새로운 현금 자산 계산
                //totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
                priceAvg = (priceAvg * uc_amount - price * existAmount + price * amount) / sumCoinAmount; //새로운 매수평단가
            }

        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            sumCoinAmount = uc_amount + existAmount - amount;
            if(sumCoinAmount < 0){
                throw new BaseException(BaseResponseStatus.AMOUNT_RANGE_ERROR);
            }
            else{
                newProperty = property - (price * existAmount) - (price * existAmount * fee) + (price * amount) + (price * amount * fee); // 새로운 현금 자산 계산
                //totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
            }
        }

        int result = tradeRepository.updateAmount(patchAmountReq);
        int propertyResult = tradeRepository.modifyProperty(newProperty, patchAmountReq.getTradeIdx());
        int userCoinResult = tradeRepository.updateUserCoinInfo(userCoinIdx, priceAvg, sumCoinAmount);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }



    // 거래내역 수정 : 수수료 수정
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateFee(TradeDto.PatchFeeReq patchFeeReq) throws BaseException{
        double fee = patchFeeReq.getFee();
        double maxFee = 100L;
        int userCoinIdx = tradeRepository.getUserCoinIdxByTradeIdx(patchFeeReq.getTradeIdx());
        List<TradeDto.GetTradeInfoRes> getTradeInfoRes = tradeRepository.getTradeInfo(patchFeeReq.getTradeIdx());
        double price = getTradeInfoRes.get(0).getPrice(); // 코인 원래 가격
        double amount = getTradeInfoRes.get(0).getAmount(); // 코인 원래 갯수
        double existFee = getTradeInfoRes.get(0).getFee(); // 코인 수수료
        String category = getTradeInfoRes.get(0).getCategory(); //매수 or 매도 : “buy”, “sell”
        double property = getTradeInfoRes.get(0).getProperty(); //원래 현금 자산
        //double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산

        double newProperty = 0; // 업데이트 해줄 새로운 현금 자산

        // 수수료 범위 validation
        if(fee<0 || fee>maxFee){
            throw new BaseException(BaseResponseStatus.FEE_RANGE_ERROR);
        }

        // 거래 내역 수정시 - 매수평단가, amount
        // 거래 내역 수정시 - 현금자산, 총자산
        if(existFee<0 || existFee>maxFee){
            throw new BaseException(BaseResponseStatus.FEE_RANGE_ERROR);
        }else if(category.equals("buy")){
            // "buy"매수라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 더해준 후 새로운 것 빼주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기.
            newProperty = property + (price * amount * existFee) - (price * amount * fee); // 새로운 현금 자산 계산
            //totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            newProperty = property- (price * amount * existFee)+ (price * amount * fee); // 새로운 현금 자산 계산
            //totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
        }

        int result = tradeRepository.updateFee(patchFeeReq);
        int propertyResult = tradeRepository.modifyProperty(newProperty, patchFeeReq.getTradeIdx());
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }



    // 거래내역 수정 : 매수/매도 수정(Patch)
    @Transactional // Trancaction 기능 : 데이터 생성,수정,삭제와같은 데이터를 작업하는 일이 여러 과정을 한번에 수행 항 때 수행을 끝마쳐야 저장, 오류나면 Rollback 해서 안전성을 부여.
    public void updateCategory(TradeDto.PatchCategoryReq patchCategoryReq) throws BaseException{
        String category = patchCategoryReq.getCategory();
        int userCoinIdx = tradeRepository.getUserCoinIdxByTradeIdx(patchCategoryReq.getTradeIdx());
        List<TradeDto.GetTradeInfoRes> getTradeInfoRes = tradeRepository.getTradeInfo(patchCategoryReq.getTradeIdx());
        double price = getTradeInfoRes.get(0).getPrice(); // 코인 원래 가격
        double amount = getTradeInfoRes.get(0).getAmount(); // 코인 원래 갯수
        double fee = getTradeInfoRes.get(0).getFee(); // 코인 수수료
        String existCategory = getTradeInfoRes.get(0).getCategory(); // 원래 매수 or 매도 : “buy”, “sell”
        double property = getTradeInfoRes.get(0).getProperty(); //원래 현금 자산
        //double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산
        double priceAvg = getTradeInfoRes.get(0).getPriceAvg(); //매수 평단가
        double uc_amount = getTradeInfoRes.get(0).getUc_amount(); //소유 코인 테이블의 코인 갯수

        double newProperty = 0; // 업데이트 해줄 새로운 현금 자산
        double sumCoinAmount = 0; //새로운 코인 전체 갯수

        // 매수/매도 null, 빈값 validation
        if(category == null || category.length()==0){
            throw new BaseException(BaseResponseStatus.EMPTY_CATEGORY);
        }

        // 거래 내역 수정시 - 매수평단가, amount
        // 거래 내역 수정시 - 현금자산, 총자산
        // 원래 매수/매도 null, 빈값 validation
        if(existCategory == null || existCategory.length()==0){
            throw new BaseException(BaseResponseStatus.EMPTY_CATEGORY);
        } else if(category.equals("buy")){
            // 새로운 카테고리가 "buy" 매수라면 현금 자산에서 빼주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기.
            newProperty = property + 2*(price * amount) + fee*price*amount; // 새로운 현금 자산 계산
            //totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
            // 매도했던 것을 매수로 바꾼것이므로 기존 매수평단가에서 수정.
            sumCoinAmount = uc_amount + 2*amount;
            priceAvg = (priceAvg * uc_amount + 2 * price * amount) / sumCoinAmount; //새로운 매수평단가
        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            newProperty = property - 2*(price * amount); // 새로운 현금 자산 계산
            //totalProperty = totalProperty - property + newProperty; // 새로운 총 자산

            sumCoinAmount = uc_amount - 2*amount;
            if(sumCoinAmount < 0){
                throw new BaseException(BaseResponseStatus.AMOUNT_RANGE_ERROR);
            }
            // 전량매도
            if(sumCoinAmount == 0){
                priceAvg = (priceAvg + price) / 2;
                int delete = userCoinRepository.deleteByUserCoinIdx(userCoinIdx);
            }
            else{
                priceAvg = (priceAvg * uc_amount - 2 * price * amount) / sumCoinAmount;
                if(priceAvg < 0){
                    throw new BaseException(MODIFY_FAIL_PRICE_AVG); //4048
                }
            }

        }

        int result = tradeRepository.updateCategory(patchCategoryReq);
        int propertyResult = tradeRepository.modifyProperty(newProperty, patchCategoryReq.getTradeIdx());
        int userCoinResult = tradeRepository.updateUserCoinInfo(userCoinIdx, priceAvg, sumCoinAmount);
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
    public void deleteTrade(TradeDto.PatchStatusReq patchStatusReq) throws BaseException, ParseException , IOException {
        int userCoinIdx = tradeRepository.getUserCoinIdxByTradeIdx(patchStatusReq.getTradeIdx());
        List<TradeDto.GetTradeInfoRes> getTradeInfoRes = this.getTradeInfo(patchStatusReq.getTradeIdx());
        double price = getTradeInfoRes.get(0).getPrice(); // 코인 원래 가격
        double amount = getTradeInfoRes.get(0).getAmount(); // 코인 원래 갯수
        double fee = getTradeInfoRes.get(0).getFee(); // 코인 수수료
        String category = getTradeInfoRes.get(0).getCategory(); //매수 or 매도 : “buy”, “sell”
        double property = getTradeInfoRes.get(0).getProperty(); //원래 현금 자산
        //double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산
        double priceAvg = getTradeInfoRes.get(0).getPriceAvg(); //매수 평단가
        double uc_amount = getTradeInfoRes.get(0).getUc_amount(); //소유 코인 테이블의 코인 갯수
        //  tradeIdx로 accountIdx 불러오고
        int accountIdx = tradeRepository.getAccountIdxByTradeIdx(patchStatusReq.getTradeIdx());

        double newProperty = 0; // 업데이트 해줄 새로운 현금 자산
        double sumCoinAmount = 0; //새로운 코인 전체 갯수

        List<UserCoinDto.GetUserCoinIdxRes> getUserCoinIdxRes = userCoinService.getUserCoinIdx(userCoinIdx);// Profit 의 삭제된 코인 심볼 찾을 때 사용
        String symbol = getUserCoinIdxRes.get(0).getSymbol();// 삭제할 유저코인의 코인실볼 미리 저장

        // 이미 삭제된 거래내역 validation
        String status = tradeRepository.getStatusByTradeIdx(patchStatusReq.getTradeIdx());
        if(status.equals("inactive")){
            throw new BaseException(BaseResponseStatus.ALREADY_DELETED_TRADE); //
        }

        if(category.equals("buy")){
            // "buy"매수라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 더해준 후 새로운 것 빼주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기.
            newProperty = property + (price * amount) + (price * amount * fee); // 새로운 현금 자산 계산
            // TODO 총자산 식 수정해야함.
            //totalProperty = totalProperty - property + newProperty - (price * amount); // 새로운 총 자산
            sumCoinAmount = uc_amount - amount; // 새로운 총 코인 갯수
            // 전량매도
            if(sumCoinAmount == 0){
                priceAvg = 0;
                int delete = userCoinRepository.deleteByUserCoinIdx(userCoinIdx);
            }
            else{
                priceAvg = (priceAvg * uc_amount - price * amount) / sumCoinAmount; //새로운 매수평단가
            }
            //TODO: 0을 나누는것 예외처리: 완료
            ////////////////////////////////////////////////////////////////
        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            newProperty = property - (price * amount) + (price * amount * fee); // 새로운 현금 자산 계산
            //totalProperty = totalProperty - property + newProperty + (price * amount); // 새로운 총 자산
            //전량매도
            if(uc_amount == 0){
                sumCoinAmount = amount;
                priceAvg = (priceAvg * 2 - price) / sumCoinAmount;
                //throw new BaseException(COIN_AMOUNT_ZERO); //4056
            }else{
                sumCoinAmount = uc_amount + amount; // 새로운 총 코인 갯수
                priceAvg = (priceAvg * uc_amount + price * amount) / sumCoinAmount; //새로운 매수평단가
            }

        }

        // 거래내역 삭제 요청
        int result = tradeRepository.deleteTrade(patchStatusReq); //
        if(uc_amount == 0) {int userCoinActive = tradeRepository.updateByUserCoinIdx(userCoinIdx);} //전량 매도였을시 inactive -> active
        int propertyResult = tradeRepository.modifyProperty(newProperty, patchStatusReq.getTradeIdx()); //계좌 수정
        int userCoinResult = tradeRepository.updateUserCoinInfo(userCoinIdx, priceAvg, sumCoinAmount); // 유저코인 수정
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 거래내역 삭제 완료되면 관련 데이터 업데이트
        // 주의: 업데이트 순서 지켜야 함

        // Profit 최근 수익내역 삭제
        //  tradeIdx로 accountIdx 불러오고
        //int accountIdx = tradeRepository.getAccountIdxByTradeIdx(patchStatusReq.getTradeIdx());

        // profit테이블 안의 accountIdx로 profitIdx 불러오기
//        ProfitDto.GetProfitReq getProfitReq = new ProfitDto.GetProfitReq(accountIdx);
//        List<ProfitDto.GetProfitRes> getProfitRes = profitService.getProfitByAccountIdx(getProfitReq);
//        int len = getProfitRes.size();
//        int profitIdx = getProfitRes.get(len-1).getProfitIdx(); // 가장 최근 수익내역 인덱스 조회
//        // Profit 내역 삭제요청
//        ProfitDto.PatchStatusReq patchProfitStatusReq = new ProfitDto.PatchStatusReq(profitIdx);
//        profitService.deleteProfit(patchProfitStatusReq)

        // 삭제하려는 거래내역의 과거 거래시각 조회
        TradeDto.Trade getTradeRes = this.getTradeByTradeIdx(patchStatusReq.getTradeIdx());
        String prevTradeDate = getTradeRes.getDate();
        //String from = "2013-04-08 10:10:10";
        // 현재 시각 구하기
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date prev = transFormat.parse(prevTradeDate);
        Date date=new Date(System.currentTimeMillis());
        //Date date = new Date();
        //date = transFormat.parse(date.toString());
        String now = transFormat.format(date);



        long diffDay = (date.getTime()- prev.getTime()) / (24*60*60*1000)+2; // 현재-과거시간 으로 날짜 차이 구하기
        //int dayCnt = date.toString().substring()
        // 해당 코인 심볼 조회
        //List<UserCoinDto.GetUserCoinIdxRes> getUserCoinIdxRes = userCoinService.getUserCoinIdx(userCoinIdx);
        //String symbol = getUserCoinIdxRes.get(0).getSymbol();
        // 해당 코인의 과거 거래일시~어제까지의 수익내역 삭제
        profitService.deleteProfitByUserCoinIdxDate(userCoinIdx,prevTradeDate);



        // 특정 계좌의 모든 코인 심볼 조회
        List<ProfitDto.GetCoinSymbolRes> getCoinSymbolRes = profitRepository.getSymbolByAccountIdx(accountIdx);
        // 코인 없는 경우 validation
        if (getCoinSymbolRes.size() == 0) {
            throw new BaseException(BaseResponseStatus.GET_SYMBOLS_NOT_EXISTS);
        }
        // 반복문으로 모든 소유 코인 들의 과거 평단가 diffDay 일수 만큼 업비트 api 조회
        double sumPrevProperty[]= new double[(int)diffDay]; // 각 날짜별 총 자산 배열
        String prevJsonTradeDate[] = new String[(int)diffDay]; //과거 거래 시각 배열
        for(int i=0;i<getCoinSymbolRes.size();i++){
            // 각 날짜별로 코인 매수가=과거 평단가*코인갯수 를 구해서 더한다.
            symbol = getCoinSymbolRes.get(i).getSymbol(); // 코인 심볼 조회

            // API 요청해서 해당 코인의 과거 거래일시~어제까지 일별 종가 시세 받아오기
            Response response = UpbitApi.getPrevClosingPrice(symbol,now,Long.toString(diffDay));
            String resultString = response.body().string();
            // 업비트 api 응답이 에러코드일 Validation
            if(resultString.charAt(0)=='{'){
                //double trade_price = rjson.get("error"); // 코인 현재 시세 평단가
                throw new BaseException(BaseResponseStatus.GET_UPBITAPI_ERROR);
            }
            // 응답 받아온 json 문자열에서 jsonObject 생성
            resultString = "{ \"dailyPrices\":"+resultString+"}";
            JSONObject rjson = new JSONObject(resultString);
            JSONArray rjsonArray = rjson.getJSONArray("dailyPrices");
            for(int j=0;j<rjsonArray.length();j++) {
                JSONObject obj = rjsonArray.getJSONObject(j);
                double prevPrice = obj.getDouble("trade_price"); // 업비트에서 전날 종가 가격 조회
                String prevJsonTradeDateTp = obj.getString("candle_date_time_utc"); // 업비트에서 과거 거래 시각 조회
                String rightPrevJsonTradeDateTp = prevJsonTradeDateTp.substring(0,10)+" "+prevJsonTradeDateTp.substring(11);
                prevJsonTradeDate[j] = rightPrevJsonTradeDateTp; // 업비트에서 과거 거래 시각 조회
                System.out.println(prevPrice);

                //TODO: 에러처리 : 처리완료
                double prevAmount = getCoinSymbolRes.get(i).getAmount(); // 코인 갯수
                double prevProperty = prevPrice*prevAmount;
                // 현재 총 자산에 더하기
                sumPrevProperty[j] += prevProperty;


            }



        }
        // 총 매수 금액 = 총 자산 - 현금
        double totalCoinProperty = getCoinSymbolRes.get(0).getTotalProperty() - getCoinSymbolRes.get(0).getProperty();
        //System.out.println(totalCoinProperty);
        // 반복문으로 일별 종가시세를 이용해서 과거 거래일시~어제까지 수익내역 새로 생성 ( createAt을 스 날 시각으로 설정)
        for(int i=0;i<diffDay;i++){
            // 총 손익금:  (현재 총 코인 자산) - 총 매수 금액
            double totalEarning = sumPrevProperty[i] - (totalCoinProperty);
            //System.out.println(totalEarning);
            // 총 수익률: ( (현재 총 코인 자산) - (총 매수 금액))/ 총 매수금액 *100
            double totalProfitRate = (sumPrevProperty[i] - totalCoinProperty) / totalCoinProperty * 100;
            //System.out.println(totalProfitRate);

            // 수익 생성 요청
            ProfitDto.PostPrevProfitReq postPrevProfitReq = new ProfitDto.PostPrevProfitReq( accountIdx,totalProfitRate,totalEarning,prevJsonTradeDate[i]); // 과거수익 시각 까지 추가
            ProfitDto.PostProfitRes postProfitRes = profitRepository.createPrevProfit(postPrevProfitReq);

            //return postProfitRes;
        }



    }

    // 거래내역 삭제 : 전체삭제
    @Transactional
    public void deleteAllTradeByUserIdx(int userIdx) throws BaseException{

        // 거래내역 삭제 요청
        int result = tradeRepository.deleteAllTradeByUserIdx(userIdx);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }


}
