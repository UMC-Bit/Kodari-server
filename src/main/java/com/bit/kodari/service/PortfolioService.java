package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.PortfolioDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.repository.portfolio.PortfolioRepository;
import com.bit.kodari.repository.usercoin.UserCoinSql;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    //포트폴리오 등록
    public PortfolioDto.PostPortfolioRes registerPortfolio(PortfolioDto.PostPortfolioReq postPortfolioReq) throws BaseException {
        //계좌 활성 상태 확인
        int accountIdx = postPortfolioReq.getAccountIdx();
        String status = portfolioRepository.getAccountStatus(accountIdx);
        List<PortfolioDto.GetAllPortfolioRes> getAllPortfolioRes = portfolioRepository.getAllPortfolio();

        if(status.equals("inactive")){
            throw new BaseException(FAILED_TO_PROPERTY_RES); //3040
        }else {
            for(int i=0; i< getAllPortfolioRes.size(); i++){
                if(getAllPortfolioRes.get(i).getUserIdx() == postPortfolioReq.getUserIdx() && getAllPortfolioRes.get(i).getAccountIdx() == accountIdx){
                    throw new BaseException(DUPLICATED_PORTFOLIO); //4050
                }
            }
        }
        try {
            PortfolioDto.PostPortfolioRes postPortfolioRes = portfolioRepository.insert(postPortfolioReq);
            return postPortfolioRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
    //포트폴리오 조회
    public  List<AccountDto.GetPropertyRes> getProperty(int accountIdx) throws BaseException {
        //status가 inactive인 account는 오류 메시지
        String status = accountRepository.getStatusByAccountIdx(accountIdx);
        if(status.equals("inactive")){
            throw new BaseException(FAILED_TO_PROPERTY_RES); //3040
        }
        try {
            List<AccountDto.GetPropertyRes> getPropertyRes = accountRepository.getProperty(accountIdx);
            return getPropertyRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
*/

    //포트폴리오 삭제
    public void deleteByPortIdx(PortfolioDto.PatchPortfolioDelReq patchPortfolioDelReq) throws BaseException{

        try {
            int result = portfolioRepository.deleteByPortIdx(patchPortfolioDelReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_PORTFOLIO); //4049
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
