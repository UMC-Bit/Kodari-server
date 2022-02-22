package com.bit.kodari.repository.ExchangeRate;

import com.bit.kodari.dto.ExchangeRateDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExchangeRateRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ExchangeRateRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }



    //환율 조회:
    public List<ExchangeRateDto.GetExchangeRateRes> getExchageRate(){
        SqlParameterSource parameterSource = new MapSqlParameterSource();

        try {
            List<ExchangeRateDto.GetExchangeRateRes> getExchangeRateRes = namedParameterJdbcTemplate.query(ExchangeRateSql.FIND_ALL,parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new ExchangeRateDto.GetExchangeRateRes(
                            rs.getInt("exchangeRateIdx"),
                            rs.getString("money"),
                            rs.getDouble("exchangePrice")// RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                    ));

            return getExchangeRateRes;
        }catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }


    // 환율 업데이트: ExchangeRateApi를 이용해 현재 환율 시세로 업데이트
    public int updateExchangePrice(double exchangePrice){
        SqlParameterSource parameterSource = new MapSqlParameterSource("exchangePrice",exchangePrice);

        return namedParameterJdbcTemplate.update(ExchangeRateSql.UPDATE_EXCHANGEPRICE,parameterSource);
    }
}
