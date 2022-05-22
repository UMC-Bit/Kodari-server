package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.RepresentDto;
import com.bit.kodari.repository.Represent.RepresentRepository;
import com.bit.kodari.repository.account.AccountRepository;
import com.bit.kodari.service.AccountService;
import com.bit.kodari.service.RepresentService;
import com.bit.kodari.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/represent")
public class RepresentController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RepresentService representService;
    @Autowired
    private RepresentRepository representRepository;
    @Autowired
    private final JwtService jwtService;

    public RepresentController(RepresentService representService, JwtService jwtService) {
        this.representService = representService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    /**
     * [POST]
     */
    // 대표 코인 등록
    //Query String
    @ResponseBody
    @PostMapping("/post")
    public BaseResponse registerAccount(@RequestBody RepresentDto.PostRepresentReq postRepresentReq) {
        int userIdx = representRepository.getUserIdxByPortIdx(postRepresentReq.getPortIdx());
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            RepresentDto.PostRepresentRes postRepresentRes = representService.registerRepresent(postRepresentReq);
            return new BaseResponse<>(postRepresentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [GET]
     */
    // 대표 코인 조회
    // Path-variable
    @ResponseBody
    @GetMapping("/{portIdx}")
    public BaseResponse<List<RepresentDto.GetRepresentRes>> getRepresent(@PathVariable("portIdx") int portIdx) {
        int userIdx = representRepository.getUserIdxByPortIdx(portIdx);
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<RepresentDto.GetRepresentRes> getRepresentRes = representService.getRepresent(portIdx);
            return new BaseResponse<>(getRepresentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * [DELETE]
     */
    // 대표 코인 삭제
    @ResponseBody
    @DeleteMapping("/del/{representIdx}")
    public BaseResponse<String> deleteByName(@PathVariable("representIdx") int representIdx) {
        int userIdx = representRepository.getUserIdxByPortIdx(representRepository.getPortIdxByRepresentIdx(representIdx));
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            RepresentDto.DeleteRepresentReq deleteRepresentReq = new RepresentDto.DeleteRepresentReq(representIdx);
            representService.delete(deleteRepresentReq);

            String result = "선택하신 대표 코인이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
