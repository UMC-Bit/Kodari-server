package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.repository.usercoin.UserCoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class UserCoinService {

    @Autowired
    private UserCoinRepository userCoinRepository;

    //소유 코인 등록
    //이미 등록되어 있는 코인은 등록할 수 없음.
    //해당 유저의 계좌인지 확인.
    public UserCoinDto.PostUserCoinRes registerUserCoin(UserCoinDto.PostUserCoinReq postUserCoinReq) throws BaseException {
        //계좌 활성 상태 확인
        int accountIdx = postUserCoinReq.getAccountIdx();
        //계좌 status
        String status = userCoinRepository.getAccountStatus(accountIdx);
        double priceAvg = postUserCoinReq.getPriceAvg();
        double amount = postUserCoinReq.getAmount();
        double max = 100000000000L;

        // accountIdx로 불러온 userIdx
        int accountUser = userCoinRepository.getAccountUserIdx(accountIdx);

        //해당 유저의 계좌인지 확인
        if(accountUser != postUserCoinReq.getUserIdx()){
            throw new BaseException(NO_MATCH_USER_ACCOUNT); //2042
        }

        //매수평단가, amount 0, 음수는 안됨, max 초과 안됨.
        if(priceAvg <= 0 || priceAvg > max){
            throw new BaseException(PRICE_AVG_RANGE_ERROR); //4052
        }else if(amount <= 0 || amount > max){
            throw new BaseException(AMOUNT_RANGE_ERROR); //4053
        }

        //계좌 활성 상태 확인
        if(status.equals("inactive")){
            throw new BaseException(FAILED_TO_PROPERTY_RES); //3040
        }
        try {
            UserCoinDto.PostUserCoinRes postUserCoinRes = userCoinRepository.insert(postUserCoinReq);
            return postUserCoinRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //소유 코인 수정
    public void updateUserCoin(UserCoinDto.PatchUserCoinReq userCoin) throws BaseException{
        //같은 유저의 같은 계좌인지 확인
        int accountIdx = userCoinRepository.getAccountIdxByUserCoinIdx(userCoin.getUserCoinIdx());
        int userIdx = userCoinRepository.getUserIdxByUserCoinIdx(userCoin.getUserCoinIdx());

        try {
            int result = userCoinRepository.updateUserCoin(userCoin);
            if(result == 0) { // 0이면 에러가 발생
                throw new BaseException(MODIFY_FAIL_USERCOIN); //4041
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //특정 코인 조회
    //매수평단가, 코인 이름, 갯수
    //소유 코인 조회
    public List<UserCoinDto.GetUserCoinIdxRes> getUserCoinIdx(int userCoinIdx) throws BaseException {
        try {
            List<UserCoinDto.GetUserCoinIdxRes> getUserCoinIdxRes = userCoinRepository.getUserCoinByUserCoinIdx(userCoinIdx);
            return getUserCoinIdxRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //소유 코인 조회
    public List<UserCoinDto.GetUserCoinRes> getUserCoin(int portIdx) throws BaseException {
        try {
            List<UserCoinDto.GetUserCoinRes> getUserCoinRes = userCoinRepository.getUserCoinByPortIdx(portIdx);
            return getUserCoinRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //소유 코인 삭제
    public void deleteByUserCoinIdx(UserCoinDto.PatchUserCoinDelReq patchUserCoinDelReq) throws BaseException{
        int userCoinIdx = patchUserCoinDelReq.getUserCoinIdx();
        try {
            int result = userCoinRepository.deleteByUserCoinIdx(userCoinIdx);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERCOIN_STATUS); //4045
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //소유 코인 전체 삭제
    // 필요없음
    public void deleteByUserIdx(UserCoinDto.PatchDelByUserIdxReq patchDelByUserIdxReq) throws BaseException{

        try {
            int result = userCoinRepository.deletebyUserIdx(patchDelByUserIdxReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_ALL_USERCOIN_STATUS); //4046
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 소유코인 삭제: 전체삭제
    @Transactional
    public void deleteAllUserCoinByUserIdx(int userIdx) throws BaseException{

        // 소유코인 삭제 요청
        int result = userCoinRepository.deleteAllUserCoinByUserIdx(userIdx);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }

    // Trade - 매수, 매도 계산(매수 평단가), 수수료 0.05%
    public void updatePriceAvg(UserCoinDto.PatchBuySellReq patchBuySellReq) throws BaseException{
        int userCoinIdx = patchBuySellReq.getUserCoinIdx();
        int coinIdx = userCoinRepository.getCoinIdxByUserCoinIdx(userCoinIdx);
        int accountIdx = userCoinRepository.getAccountIdxByUserCoinIdx(userCoinIdx);
        int portIdx = userCoinRepository.getPortIdx(accountIdx); //빼야함
        int tradeIdx = userCoinRepository.getTradeIdx(coinIdx, portIdx); // portIdx를 accountIdx로 바꿔야함
        String category = userCoinRepository.getCategory(tradeIdx); //매수인지 매도인지
        double price = userCoinRepository.getPrice(tradeIdx);
        double newCoinAmount = userCoinRepository.getAmount(tradeIdx); //새로 산 코인 갯수
        double fee = userCoinRepository.getFee(tradeIdx);

        //기존 총자산
        double totalProperty = userCoinRepository.getTotalProperty(accountIdx);
        //기존 현금자산
        double property = userCoinRepository.getProperty(accountIdx);
        //기존 코인 갯수
        double existCoinAmount = userCoinRepository.getAmountByUserCoinIdx(userCoinIdx);
        //기존 매수평단가
        double priceAvg = userCoinRepository.getPriceAvg(userCoinIdx);
        double total = 0; //새로운 매수평단가
        double sumCoinAmount = 0; //새로운 코인 전체 갯수
        double newTotal = 0; //새로운 총자산

        //매수일때
        if(category.equals("buy")){
            //매수평단가 새로 계산해서 업데이트
            sumCoinAmount = existCoinAmount + newCoinAmount; //새로운 코인 갯수
            //새로운 매수평단가
            total = (priceAvg * existCoinAmount + price * newCoinAmount) / sumCoinAmount;
        }else if(category.equals("sell")) {
            sumCoinAmount = existCoinAmount - newCoinAmount;
            // 기존 수량보다 new가 더 크면 안됨.
            if(sumCoinAmount < 0){
                throw new BaseException(COIN_AMOUNT_OVER); //4054
            }
            //전량매도
            if(sumCoinAmount == 0){
                total = (priceAvg + price) / 2;
                int delete = userCoinRepository.deleteByUserCoinIdx(userCoinIdx);
                //throw new BaseException(COIN_AMOUNT_ZERO); //4056
            }
            else{
                //새로운 매수평단가
                total = (priceAvg * existCoinAmount - price * newCoinAmount) / sumCoinAmount;
                if(total < 0){
                    throw new BaseException(MODIFY_FAIL_PRICE_AVG); //4048
                }
            }

        }
        else{
            throw new BaseException(MODIFY_FAIL_PRICE_AVG); //4048
        }

        try {
            int result = userCoinRepository.updatePriceAvg(userCoinIdx, total, sumCoinAmount);
            if(result == 0) { // 0이면 에러가 발생
                throw new BaseException(MODIFY_FAIL_PRICE_AVG); //4048
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
