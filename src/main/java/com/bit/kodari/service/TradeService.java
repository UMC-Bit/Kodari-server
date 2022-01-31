package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.repository.account.AccountRepository;
import com.bit.kodari.repository.coin.CoinRepository;
import com.bit.kodari.repository.portfolio.PortfolioRepository;
import com.bit.kodari.repository.trade.TradeRepository;
import com.bit.kodari.repository.user.UserRepository;
import com.bit.kodari.repository.usercoin.UserCoinRepository;
import com.bit.kodari.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



    @Autowired //readme 참고
    public TradeService(TradeRepository tradeRepository, JwtService jwtService, AccountService accountService, PortfolioRepository portfolioRepository, AccountRepository accountRepository
    ,UserCoinRepository userCoinRepository) {
        this.tradeRepository = tradeRepository;
        this.jwtService = jwtService; // JWT부분
        this.accountService = accountService;
        this.portfolioRepository = portfolioRepository;
        this.accountRepository = accountRepository;
        this.userCoinRepository = userCoinRepository;
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

        // 매수를 진행할 때 사용자의 계좌의 돈이 부족한 경우 Validation
        if(category.equals("buy")){
            // 계좌의 현금 - (매수하는 코인 평단가 * 갯수) - 수수료 < 0
            // Trade에서 portIdx를 통해 Portfolio(accountIdx) 구하기
            // Account에서 property 구하기
            int accountIdx = portfolioRepository.getAccountIdx(portIdx);
            double cashProperty = accountRepository.getPropertyByAccount(accountIdx);
            if((cashProperty - price*amount - price*amount*fee)<0){
                throw new BaseException(BaseResponseStatus.LACK_OF_PROPERTY);
            }
        }

        // 매도를 진행할 때 매도량이 사용자의 소유코인 개수보다 더 많을경우 Validation
        else if(category.equals("sell")){
            int accountIdx = portfolioRepository.getAccountIdx(portIdx);
            // coinIdx, accountIdx로 UserCoin.amount 조회
            //  소유코인갯수 - 매도량  < 0 면 소유코인 갯수 부족 에러
            double userCoinAmount = tradeRepository.getUserCoinAmountByAccountIdxCoinIdx(accountIdx,coinIdx);
            if((userCoinAmount - amount)<0){
                throw new BaseException(BaseResponseStatus.LACK_OF_AMOUNT);
            }
        }

        // 입력값 검증되면 생성 요청
        try {
            // 거래내역 생성 요청
            TradeDto.PostTradeRes postTradeRes = tradeRepository.createTrade(postTradeReq);

            // 매수,매도 거래내역 등록 완료 시 Account 보유현금, 총 자산 자동 업데이트
            accountService.updateTradeProperty(postTradeRes.getTradeIdx());
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
        double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산
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
            totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
            priceAvg = (priceAvg * uc_amount - price * amount + newPrice * amount) / uc_amount; //새로운 매수평단가
        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            newProperty = property - (price * amount) - (price * amount * fee) + (newPrice * amount) + (newPrice * amount * fee); // 새로운 현금 자산 계산
            totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
        }

        int result = tradeRepository.updatePrice(patchPriceReq);
        if(category.equals("buy")){
            int propertyResult = tradeRepository.modifyProperty(newProperty, totalProperty, patchPriceReq.getTradeIdx());
            int priceAvgResult = tradeRepository.updatePriceAvg(userCoinIdx, priceAvg);
        }else if(category.equals("sell")){
            int propertyResult = tradeRepository.modifyProperty(newProperty, totalProperty, patchPriceReq.getTradeIdx());
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
        double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산
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
                totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
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
                totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
            }
        }

        int result = tradeRepository.updateAmount(patchAmountReq);
        int propertyResult = tradeRepository.modifyProperty(newProperty, totalProperty, patchAmountReq.getTradeIdx());
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
        double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산

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
            totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            newProperty = property- (price * amount * existFee)+ (price * amount * fee); // 새로운 현금 자산 계산
            totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
        }

        int result = tradeRepository.updateFee(patchFeeReq);
        int propertyResult = tradeRepository.modifyProperty(newProperty, totalProperty, patchFeeReq.getTradeIdx());
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
        double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산
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
            newProperty = property - 2*(price * amount); // 새로운 현금 자산 계산
            totalProperty = totalProperty - property + newProperty; // 새로운 총 자산
            // 매도했던 것을 매수로 바꾼것이므로 기존 매수평단가에서 수정.
            sumCoinAmount = uc_amount + 2*amount;
            priceAvg = (priceAvg * uc_amount + 2 * price * amount) / sumCoinAmount; //새로운 매수평단가
        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            newProperty = property + 2*(price * amount); // 새로운 현금 자산 계산
            totalProperty = totalProperty - property + newProperty; // 새로운 총 자산

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
        int propertyResult = tradeRepository.modifyProperty(newProperty, totalProperty, patchCategoryReq.getTradeIdx());
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
    public void deleteTrade(TradeDto.PatchStatusReq patchStatusReq) throws BaseException{
        int userCoinIdx = tradeRepository.getUserCoinIdxByTradeIdx(patchStatusReq.getTradeIdx());
        List<TradeDto.GetTradeInfoRes> getTradeInfoRes = tradeRepository.getTradeInfo(patchStatusReq.getTradeIdx());
        double price = getTradeInfoRes.get(0).getPrice(); // 코인 원래 가격
        double amount = getTradeInfoRes.get(0).getAmount(); // 코인 원래 갯수
        double fee = getTradeInfoRes.get(0).getFee(); // 코인 수수료
        String category = getTradeInfoRes.get(0).getCategory(); //매수 or 매도 : “buy”, “sell”
        double property = getTradeInfoRes.get(0).getProperty(); //원래 현금 자산
        double totalProperty = getTradeInfoRes.get(0).getTotalProperty(); // 원래 총자산
        double priceAvg = getTradeInfoRes.get(0).getPriceAvg(); //매수 평단가
        double uc_amount = getTradeInfoRes.get(0).getUc_amount(); //소유 코인 테이블의 코인 갯수

        double newProperty = 0; // 업데이트 해줄 새로운 현금 자산
        double sumCoinAmount = 0; //새로운 코인 전체 갯수

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
            totalProperty = totalProperty - property + newProperty - (price * amount); // 새로운 총 자산
            sumCoinAmount = uc_amount - amount; // 새로운 총 코인 갯수
            priceAvg = (priceAvg * uc_amount - price * amount) / sumCoinAmount; //새로운 매수평단가
        }else if(category.equals("sell")){
            // "sell" 매도라면 현금 자산에서 원래 코인 가격, 갯수, 수수료만큼 뺀 후 새로운 것 더해주기
            // 총자산은 원래 현금자산 빼주고 새로운 현금자산 더해주기 -> (매수, 매도 똑같음)
            newProperty = property - ((price * amount) + (price * amount * fee)); // 새로운 현금 자산 계산
            totalProperty = totalProperty - property + newProperty + (price * amount); // 새로운 총 자산
            sumCoinAmount = uc_amount + amount; // 새로운 총 코인 갯수
            priceAvg = (priceAvg * uc_amount + price * amount) / sumCoinAmount; //새로운 매수평단가
        }

        // 거래내역 삭제 요청
        int result = tradeRepository.deleteTrade(patchStatusReq);
        int propertyResult = tradeRepository.modifyProperty(newProperty, totalProperty, patchStatusReq.getTradeIdx());
        int userCoinResult = tradeRepository.updateUserCoinInfo(userCoinIdx, priceAvg, sumCoinAmount);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }

    // 거래내역 삭제 : 전체삭제
    @Transactional
    public void deleteAllTradeByUserIdx(int userIdx) throws BaseException{
        // 이미 삭제된 거래내역 validation
        /*String status = tradeRepository.getStatusByTradeIdx(patchStatusReq.getTradeIdx());
        if(status.equals("inactive")){
            throw new BaseException(BaseResponseStatus.ALREADY_DELETED_TRADE); //
        }*/
        // 거래내역 삭제 요청
        int result = tradeRepository.deleteAllTradeByUserIdx(userIdx);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }


}
