package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.BoardDto;
import com.bit.kodari.dto.CoinDto;
import com.bit.kodari.repository.board.BoardRepository;
import com.bit.kodari.repository.coin.CoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
public class CoinService {
    @Autowired
    CoinRepository coinRepository;

    // 토론장 전체 카테고리 조회
    public List<CoinDto.GetCoinRes> getCoins() throws BaseException {
        try {
            List<CoinDto.GetCoinRes> getCoinRes = coinRepository.getCoins();
            return getCoinRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
