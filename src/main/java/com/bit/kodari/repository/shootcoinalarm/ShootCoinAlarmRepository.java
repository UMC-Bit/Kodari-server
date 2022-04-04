package com.bit.kodari.repository.shootcoinalarm;


import com.bit.kodari.dto.RegisterCoinAlarmDto;
import com.bit.kodari.dto.ShootCoinAlarmDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShootCoinAlarmRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    ShootCoinAlarmSql shootCoinAlarmSql;
    public ShootCoinAlarmRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //코인 시세 알림 등록
    public ShootCoinAlarmDto.RegisterRes insert(ShootCoinAlarmDto.RegisterReq coinalarm) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", coinalarm.getUserIdx())
                .addValue("marketIdx", coinalarm.getMarketIdx())
                .addValue("coinIdx", coinalarm.getCoinIdx())
                .addValue("growth", coinalarm.getGrowth())
                .addValue("decline", coinalarm.getDecline());
        int affectedRows = namedParameterJdbcTemplate.update(shootCoinAlarmSql.INSERT_COIN_ALARM, parameterSource, keyHolder);
        return ShootCoinAlarmDto.RegisterRes.builder().userIdx(coinalarm.getUserIdx()).build();
    }

    //한 코인 당 등록된 시세알림 수 구하기
    public int getAlarmByCoinIdx(int userIdx, int marketIdx, int coinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("marketIdx", marketIdx)
                .addValue("coinIdx", coinIdx);
        return namedParameterJdbcTemplate.query(shootCoinAlarmSql.GET_REGISTER_ALARM_COUNT, parameterSource, rs -> {
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
        return namedParameterJdbcTemplate.query(shootCoinAlarmSql.GET_REGISTER_MARKET_ALARM_COUNT, parameterSource, rs -> {
            int coin_count = 0;
            if (rs.next()) {
                coin_count = rs.getInt("coin_count");
            }

            return coin_count;
        });
    }

    //같은 값의 알림 존재 여부
    public boolean getExistAlarm(int userIdx, int marketIdx, int coinIdx, double growth, double decline) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("marketIdx", marketIdx)
                .addValue("coinIdx", coinIdx)
                .addValue("growth", growth)
                .addValue("decline", decline);
        return namedParameterJdbcTemplate.query(shootCoinAlarmSql.GET_EXIST_ALARM, parameterSource, rs -> {
            boolean exist = false;
            if (rs.next()) {
                exist = rs.getBoolean("exist");
            }

            return exist;
        });
    }

    //코인 시세 알림 수정
    public int modifyCoinAlarm(ShootCoinAlarmDto.PatchCoinAlarmReq patchReq) {
        String qry = shootCoinAlarmSql.UPDATE_REGISTER_COIN_ALARM;
        SqlParameterSource parameterSource = new MapSqlParameterSource("shootCoinAlarmIdx", patchReq.getShootCoinAlarmIdx())
                .addValue("growth", patchReq.getGrowth())
                .addValue("decline", patchReq.getDecline());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //shootCoinAlarmIdx로 userIdx 가져오기
    public int getUserIdxByShootCoinAlarmIdx(int shootCoinAlarmIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("shootCoinAlarmIdx", shootCoinAlarmIdx);
        return namedParameterJdbcTemplate.query(shootCoinAlarmSql.GET_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    //shootCoinAlarmIdx로 marketIdx 가져오기
    public int getMarketIdxByShootCoinAlarmIdx(int shootCoinAlarmIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("shootCoinAlarmIdx", shootCoinAlarmIdx);
        return namedParameterJdbcTemplate.query(shootCoinAlarmSql.GET_MARKET_IDX, parameterSource, rs -> {
            int marketIdx = 0;
            if (rs.next()) {
                marketIdx = rs.getInt("marketIdx");
            }

            return marketIdx;
        });
    }

    //shootCoinAlarmIdx로 coinIdx 가져오기
    public int getCoinIdxByShootCoinAlarmIdx(int shootCoinAlarmIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("shootCoinAlarmIdx", shootCoinAlarmIdx);
        return namedParameterJdbcTemplate.query(shootCoinAlarmSql.GET_COIN_IDX, parameterSource, rs -> {
            int coinIdx = 0;
            if (rs.next()) {
                coinIdx = rs.getInt("coinIdx");
            }

            return coinIdx;
        });
    }

    //코인 시세 알림 삭제
    public int modifyAlarmStatus(ShootCoinAlarmDto.PatchDeleteReq patchDeleteReq) {
        String qry = shootCoinAlarmSql.DELETE_ALARM;
        SqlParameterSource parameterSource = new MapSqlParameterSource("shootCoinAlarmIdx", patchDeleteReq.getShootCoinAlarmIdx());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //유저별 폭락, 폭등 조회
    public ShootCoinAlarmDto.GetUserCoinAlarmRes getAlarms(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        try {
            ShootCoinAlarmDto.GetUserCoinAlarmRes getCoinAlarms = namedParameterJdbcTemplate.queryForObject(shootCoinAlarmSql.LIST_COIN_ALARM, parameterSource,
                    (rs, rowNum) -> {
                        List<ShootCoinAlarmDto.GetMarketRes> marketList = getMarketByUserIdx(userIdx);
                        ShootCoinAlarmDto.GetUserCoinAlarmRes coinalarm = new ShootCoinAlarmDto.GetUserCoinAlarmRes
                                (rs.getInt("userIdx"), marketList); // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                        return coinalarm;
                    }
            );
            return getCoinAlarms;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    //폭락, 폭등 알림 조회 시 마켓 정보 조회
    public List<ShootCoinAlarmDto.GetMarketRes> getMarketByUserIdx(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        try{
            List<ShootCoinAlarmDto.GetMarketRes> getMarketRes = namedParameterJdbcTemplate.query(shootCoinAlarmSql.LIST_MARKET, parameterSource,
                    (rs, rowNum) -> new ShootCoinAlarmDto.GetMarketRes(
                            rs.getInt("marketIdx"),
                            rs.getString("marketName"),getCoinByUserIdx(userIdx, rs.getInt("marketIdx"))

                    ));
            // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받
            return getMarketRes;
        }catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    //폭락, 폭등 알림 조회 시 코인 정보 조회
    public List<ShootCoinAlarmDto.GetCoinRes> getCoinByUserIdx(int userIdx, int marketIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("marketIdx", marketIdx);
        try{
            List<ShootCoinAlarmDto.GetCoinRes> getCoinRes = namedParameterJdbcTemplate.query(shootCoinAlarmSql.LIST_COIN, parameterSource,
                    (rs, rowNum) -> new ShootCoinAlarmDto.GetCoinRes(
                            rs.getInt("coinIdx"),
                            rs.getString("coinName"),
                            rs.getString("symbol"),
                            rs.getString("coinImg"),getAlarmByUserIdx(userIdx, marketIdx, rs.getInt("coinIdx"))

                    ));
            // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받
            return getCoinRes;
        }catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    //폭락, 폭등 알림 조회 시 해당 알림 정보 조회
    public List<ShootCoinAlarmDto.GetAlarmRes> getAlarmByUserIdx(int userIdx, int marketIdx, int coinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("marketIdx", marketIdx)
                .addValue("coinIdx", coinIdx);
        try{
            List<ShootCoinAlarmDto.GetAlarmRes> getAlarmRes = namedParameterJdbcTemplate.query(shootCoinAlarmSql.LIST_ALARM, parameterSource,
                    (rs, rowNum) -> new ShootCoinAlarmDto.GetAlarmRes(
                            rs.getInt("shootCoinAlarmIdx"),
                            rs.getDouble("growth"),
                            rs.getDouble("decline")

                    ));
            // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받
            return getAlarmRes;
        }catch(EmptyResultDataAccessException e) {
            return null;
        }
    }





}
