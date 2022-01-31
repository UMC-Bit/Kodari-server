package com.bit.kodari.repository.coin;

import com.bit.kodari.dto.CoinDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CoinRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    CoinSql coinSql;
    public CoinRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 코인 전체 조회
    public List<CoinDto.GetCoinRes> getCoins(){
        SqlParameterSource parameterSource = new MapSqlParameterSource();
        List<CoinDto.GetCoinRes> getCoinRes = namedParameterJdbcTemplate.query(CoinSql.LIST_COIN,parameterSource,
                (rs, rowNum) -> new CoinDto.GetCoinRes(
                        rs.getInt("coinIdx"),
                        rs.getString("coinName"),
                        rs.getString("symbol"),
                        rs.getString("coinImg")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getCoinRes;
    }

    //토론장 특정 코인 이름 조회
    public List<CoinDto.GetCoinRes> getCoinsByCoinName(String coinName) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("coinName", coinName);
        List<CoinDto.GetCoinRes> getCoinRes = namedParameterJdbcTemplate.query(coinSql.LIST_COIN_NAME, parameterSource,
                (rs, rowNum) -> new CoinDto.GetCoinRes(
                        rs.getInt("coinIdx"),
                        rs.getString("coinName"),
                        rs.getString("symbol"),
                        rs.getString("coinImg"))
        );
        return getCoinRes;
    }

}
