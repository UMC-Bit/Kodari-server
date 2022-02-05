package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.RepresentDto;
import com.bit.kodari.repository.Represent.RepresentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;
import static com.bit.kodari.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
public class RepresentService {
    @Autowired
    private RepresentRepository representRepository;

    // 대표 코인 등록
    public RepresentDto.PostRepresentRes registerRepresent(RepresentDto.PostRepresentReq postRepresentReq) throws BaseException {
        List<RepresentDto.GetRepresentIdxRes> getRepresentIdxRes = representRepository.getRepresentIdxRes(postRepresentReq.getPortIdx());
        // 포트폴리오 활성 상태 확인
        String status = representRepository.getStatusByPortIdx(postRepresentReq.getPortIdx());
        if(status.equals("inactive")){
            throw new BaseException(INACTIVE_PORTFOLIO); //2041
        }
        for(int i=0; i < getRepresentIdxRes.size(); i++){
            if(getRepresentIdxRes.get(i).getCoinIdx() == postRepresentReq.getCoinIdx()){
                throw new BaseException(ALREADY_REPRESENT); //4058
            }
        }

        try {
            RepresentDto.PostRepresentRes postRepresentRes = representRepository.insert(postRepresentReq);
            return postRepresentRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 대표 코인 조회
    // 코인이름, 코인 심볼, 코인 이미지 추가하기
    public List<RepresentDto.GetRepresentRes> getRepresent(int portIdx) throws BaseException {
        try {
            List<RepresentDto.GetRepresentRes> getRepresentRes = representRepository.getRepresent(portIdx);
            return getRepresentRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //보류 - 대표 코인 단일 조회


    // 대표 코인 삭제
    public void delete(RepresentDto.DeleteRepresentReq deleteRepresentReq) throws BaseException{

        try {
            int result = representRepository.delete(deleteRepresentReq);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_REPRESENT); //4055
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }



    // 대표 코인 삭제 : 전체삭제
    @Transactional
    public void deleteAllReprsentByUserIdx(int userIdx) throws BaseException{

        // 수익내역 삭제 요청
        int result = representRepository.deleteAllReprsentByUserIdx(userIdx);
        if (result == 0) {// result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
    }
}
