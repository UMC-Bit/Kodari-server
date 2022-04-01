package com.bit.kodari.repository.registerCoinAlarm;


import com.bit.kodari.dto.RegisterCoinAlarmDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RegisterCoinAlarmRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    RegisterCoinAlarmSql registerCoinAlarmSql;
    public RegisterCoinAlarmRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /*
   특정 코인 지정가격 조회: marketIdx, coinIdx 로 조회
    */
    // 등록된 지정가격 조회:
    public List<RegisterCoinAlarmDto.GetRegisterCoinAlarmRes> getRegisterCoinAlarmPriceByMarketIdxCoinIdx(int marketIdx, int coinIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("marketIdx",marketIdx)
                .addValue("coinIdx",coinIdx);


        List<RegisterCoinAlarmDto.GetRegisterCoinAlarmRes> getRegisterCoinAlarmRes = namedParameterJdbcTemplate.query(RegisterCoinAlarmSql.FIND_BY_MARKETIDX_COINIDX,parameterSource,
                // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                (rs, rowNum) -> new RegisterCoinAlarmDto.GetRegisterCoinAlarmRes(
                        rs.getInt("registerCoinAlarmIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("marketIdx"),
                        rs.getInt("coinIdx"),
                        rs.getDouble("targetPrice"),
                        rs.getString("status")// RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                ));

        return getRegisterCoinAlarmRes;

//        catch (EmptyResultDataAccessException e) {
//            // EmptyResultDataAccessException 예외 발생시 null 리턴
//            return null;
//        }
    }

}
