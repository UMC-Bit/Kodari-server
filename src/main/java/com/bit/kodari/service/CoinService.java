package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.BoardDto;
import com.bit.kodari.dto.CoinDto;
import com.bit.kodari.repository.board.BoardRepository;
import com.bit.kodari.repository.coin.CoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
public class CoinService {
    @Autowired
    CoinRepository coinRepository;


    // 토론장 전체 카테고리 조회
    @Transactional
    public List<CoinDto.GetCoinRes> getCoins() throws BaseException {
        try {
            List<CoinDto.GetCoinRes> getCoinRes = coinRepository.getCoins();
            return getCoinRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 토론장 코인이름 조회
    @Transactional
    public List<CoinDto.GetCoinRes> getCoinsByCoinName(String coinName) throws BaseException {
        try {
            List<CoinDto.GetCoinRes> getCoinsRes = coinRepository.getCoinsByCoinName(coinName);
            return getCoinsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //marketIdx별 코인 조회
    @Transactional
    public List<CoinDto.GetCoinRes> getCoinsByMarket(int marketIdx) throws BaseException {
        try {
            List<CoinDto.GetCoinRes> getCoinsRes = coinRepository.getCoinsByMarket(marketIdx);
            return getCoinsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // marketIdx별 코인 이름 조회
    @Transactional
    public List<CoinDto.GetCoinRes> getMarketCoinByCoinName(int marketIdx, String coinName) throws BaseException {
        try {
            List<CoinDto.GetCoinRes> getCoinsRes = coinRepository.getMarketCoinByCoinName(marketIdx, coinName);
            return getCoinsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // marketIdx별 코인 심볼 조회
    @Transactional
    public List<CoinDto.GetCoinRes> getMarketCoinBySymbol(int marketIdx, String symbol) throws BaseException {
        try {
            List<CoinDto.GetCoinRes> getCoinsRes = coinRepository.getMarketCoinBySymbol(marketIdx, symbol);
            return getCoinsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
