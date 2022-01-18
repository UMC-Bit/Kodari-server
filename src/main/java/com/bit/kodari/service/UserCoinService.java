package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.repository.usercoin.UserCoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class UserCoinService {

    @Autowired
    private UserCoinRepository userCoinRepository;

    //소유 코인 등록
    public UserCoinDto.PostUserCoinRes registerUserCoin(UserCoinDto.PostUserCoinReq postUserCoinReq) throws BaseException {
        //계좌 활성 상태 확인
        int accountIdx = postUserCoinReq.getAccountIdx();
        String status = userCoinRepository.getAccountStatus(accountIdx);
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
    public List<UserCoinDto.GetUserCoinRes> getUserCoin(int userIdx) throws BaseException {
        try {
            List<UserCoinDto.GetUserCoinRes> getUserCoinRes = userCoinRepository.getUserCoinByUserIdx(userIdx);
            return getUserCoinRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //소유 코인 삭제
    public void deleteByUserCoinIdx(UserCoinDto.PatchUserCoinDelReq patchUserCoinDelReq) throws BaseException{

        try {
            int result = userCoinRepository.deleteByUserCoinIdx(patchUserCoinDelReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERCOIN_STATUS); //4045
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //소유 코인 전체 삭제
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

    //매수, 매도 계산(매수 평단가), 수수료 0.05%
    //계산하는거 여기에
    public void updatePriceAvg(UserCoinDto.PatchBuySellReq patchBuySellReq) throws BaseException{
        int userCoinIdx = patchBuySellReq.getUserCoinIdx();
        int coinIdx = userCoinRepository.getCoinIdxByUserCoinIdx(userCoinIdx);
        int accountIdx = userCoinRepository.getAccountIdxByUserCoinIdx(userCoinIdx);
        int portIdx = userCoinRepository.getPortIdx(accountIdx); //빼야함
        int tradeIdx = userCoinRepository.getTradeIdx(coinIdx, portIdx); // portIdx를 accountIdx로 바꿔야함
        String category = userCoinRepository.getCategory(tradeIdx); //매수인지 매도인지
        double price = userCoinRepository.getPrice(tradeIdx);
        double amount2 = userCoinRepository.getAmount(tradeIdx); //새로 산 코인 갯수
        double fee = userCoinRepository.getFee(tradeIdx);

        //기존 코인 갯수
        double amount1 = userCoinRepository.getAmountByUserCoinIdx(userCoinIdx);
        //기존 매수평단가
        double priceAvg = userCoinRepository.getPriceAvg(userCoinIdx);
        double total = 0;
        double amount3 = 0;

        //매수일때
        if(category.equals("buy")){
            //매수평단가 새로 계산해서 업데이트
            amount3 = amount1 + amount2;
            total = (priceAvg * amount1 + price * amount2) / amount3;
        }else if(category.equals("sell")) {
            amount3 = amount1 - amount2;
            total = (priceAvg * amount1 - price * amount2) / amount3;
        }
        else{
            throw new BaseException(MODIFY_FAIL_PRICE_AVG); //4048
        }

        try {
            int result = userCoinRepository.updatePriceAvg(userCoinIdx, total, amount3);
            if(result == 0) { // 0이면 에러가 발생
                throw new BaseException(MODIFY_FAIL_PRICE_AVG); //4048
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
