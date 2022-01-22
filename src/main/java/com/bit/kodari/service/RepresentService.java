package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.RepresentDto;
import com.bit.kodari.repository.Represent.RepresentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // 포트폴리오 활성 상태 확인
        String status = representRepository.getStatusByPortIdx(postRepresentReq.getPortIdx());
        if(status.equals("inactive")){
            throw new BaseException(INACTIVE_PORTFOLIO); //2041
        }
        try {
            RepresentDto.PostRepresentRes postRepresentRes = representRepository.insert(postRepresentReq);
            return postRepresentRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 대표 코인 조회
    public List<RepresentDto.GetRepresentRes> getRepresent(int portIdx) throws BaseException {
        try {
            List<RepresentDto.GetRepresentRes> getRepresentRes = representRepository.getRepresent(portIdx);
            return getRepresentRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

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
}
