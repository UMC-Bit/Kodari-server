package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.PortfolioDto;
import com.bit.kodari.repository.portfolio.PortfolioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    //포트폴리오 등록
    //비티코인, 이더리움, 솔라나 로 자동 넣어주기
    public PortfolioDto.PostPortfolioRes registerPortfolio(PortfolioDto.PostPortfolioReq postPortfolioReq) throws BaseException {
        //계좌 활성 상태 확인
        int accountIdx = postPortfolioReq.getAccountIdx();
        String status = portfolioRepository.getAccountStatus(accountIdx);

        List<PortfolioDto.GetAllPortfolioRes> getAllPortfolioRes = portfolioRepository.getAllPortfolio();
        // userIdx로 모든 portIdx 가져오기
        List<PortfolioDto.GetAllPortIdxRes> getAllPortIdxRes = portfolioRepository.getAllPortIdx(postPortfolioReq.getUserIdx());
        // 포트폴리오 3개 초과 생성 안됨
        if(getAllPortIdxRes.size() > 3){
            throw new BaseException(OVER_PORT_THREE); //3041
        }
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
            int portIdx = postPortfolioRes.getPortIdx();
            //비트코인
            PortfolioDto.GetRepresentRes getRepresentRes1 = portfolioRepository.insertRepresent(portIdx, 16);
            //이더리움
            PortfolioDto.GetRepresentRes getRepresentRes2 = portfolioRepository.insertRepresent(portIdx, 32);
            //솔라나
            PortfolioDto.GetRepresentRes getRepresentRes3 = portfolioRepository.insertRepresent(portIdx, 82);
            return postPortfolioRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }


    //포트폴리오 조회
    // userCoinIdx 대괄호로 쫘르륵...? -> LIST
    // 수익률, 소득, 대표코인 (코인별로)
    public PortfolioDto.GetPortfolioRes getPortfolio(int portIdx) throws BaseException {
        //status가 inactive인 account는 오류 메시지
        //String status = accountRepository.getStatusByAccountIdx(accountIdx);
        //if(status.equals("inactive")){
            //throw new BaseException(FAILED_TO_PROPERTY_RES); //3040
        //}
        try {
            PortfolioDto.GetPortfolioRes getPortfolioRes = portfolioRepository.getPortfolio(portIdx);
            return getPortfolioRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 포트폴리오 전체 조회 userIdx
    public List<PortfolioDto.GetAllPortIdxRes> getPortListByUserIdx(int userIdx) throws BaseException {
        try{
            List<PortfolioDto.GetAllPortIdxRes> getAllPortByUserRes = portfolioRepository.getAllPortByUserIdx(userIdx);
            return getAllPortByUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //포트폴리오 삭제
    public void deleteByPortIdx(PortfolioDto.PatchPortfolioDelReq patchPortfolioDelReq) throws BaseException{
        int portIdx = patchPortfolioDelReq.getPortIdx();
        int accountIdx = portfolioRepository.getAccountIdx(portIdx);
        int userIdx = portfolioRepository.getUserIdxByPortIdx(portIdx);
        try {
            int result = portfolioRepository.deleteByPortIdx(portIdx, accountIdx, userIdx);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_PORTFOLIO); //4049
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
