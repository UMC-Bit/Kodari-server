package com.bit.kodari.repository.registerCoinAlarm;


import com.bit.kodari.dto.RegisterCoinAlarmDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RegisterCoinAlarmRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    RegisterCoinAlarmSql registerCoinAlarmSql;
    public RegisterCoinAlarmRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //코인 시세 알림 등록
    public RegisterCoinAlarmDto.RegisterRes insert(RegisterCoinAlarmDto.RegisterReq coinalarm) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", coinalarm.getUserIdx())
                .addValue("marketIdx", coinalarm.getMarketIdx())
                .addValue("coinIdx", coinalarm.getCoinIdx())
                .addValue("targetPrice", coinalarm.getTargetPrice());
        int affectedRows = namedParameterJdbcTemplate.update(registerCoinAlarmSql.INSERT_COIN_ALARM, parameterSource, keyHolder);
        return RegisterCoinAlarmDto.RegisterRes.builder().userIdx(coinalarm.getUserIdx()).build();
    }

    //한 코인 당 등록된 시세알림 수 구하기
    public int getAlarmByCoinIdx(int userIdx, int marketIdx, int coinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("marketIdx", marketIdx)
                .addValue("coinIdx", coinIdx);
        return namedParameterJdbcTemplate.query(registerCoinAlarmSql.GET_REGISTER_ALARM_COUNT, parameterSource, rs -> {
            int alarm_count = 0;
            if (rs.next()) {
                alarm_count = rs.getInt("alarm_count");
            }

            return alarm_count;
        });
    }

    //market별 등록된 코인 수 구하기
    public int getCoinByMarketIdx(int userIdx, int marketIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("marketIdx", marketIdx);
        return namedParameterJdbcTemplate.query(registerCoinAlarmSql.GET_REGISTER_MARKET_ALARM_COUNT, parameterSource, rs -> {
            int alarm_count = 0;
            if (rs.next()) {
                alarm_count = rs.getInt("alarm_count");
            }

            return alarm_count;
        });
    }

    //코인 시세 알림 수정
    public int modifyCoinAlarm(RegisterCoinAlarmDto.PatchCoinAlarmReq patchReq) {
        String qry = registerCoinAlarmSql.UPDATE_REGISTER_COIN_ALARM;
        SqlParameterSource parameterSource = new MapSqlParameterSource("registerCoinAlarmIdx", patchReq.getRegisterCoinAlarmIdx())
                .addValue("targetPrice", patchReq.getTargetPrice());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //registerCoinAlarmIdx로 userIdx 가져오기
    public int getUserIdxByRegisterCoinAlarmIdx(int registerCoinAlarmIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("registerCoinAlarmIdx", registerCoinAlarmIdx);
        return namedParameterJdbcTemplate.query(registerCoinAlarmSql.GET_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    //코인 시세 알림 삭제
    public int modifyAlarmStatus(RegisterCoinAlarmDto.PatchDeleteReq patchDeleteReq) {
        String qry = registerCoinAlarmSql.DELETE_ALARM;
        SqlParameterSource parameterSource = new MapSqlParameterSource("registerCoinAlarmIdx", patchDeleteReq.getRegisterCoinAlarmIdx());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }


    //유저별 코인 시세 알림 조회
    public List<RegisterCoinAlarmDto.GetUserCoinAlarmRes> getAlarms(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        List<RegisterCoinAlarmDto.GetUserCoinAlarmRes> getAlarmRes = namedParameterJdbcTemplate.query(registerCoinAlarmSql.LIST_ALARM,parameterSource,
                (rs, rowNum) -> new RegisterCoinAlarmDto.GetUserCoinAlarmRes(
                        rs.getInt("registerCoinAlarmIdx"),
                        rs.getString("marketName"),
                        rs.getString("coinName"),
                        rs.getString("symbol"),
                        rs.getString("coinImg"),
                        rs.getDouble("targetPrice")));

        return getAlarmRes;

    }




}
