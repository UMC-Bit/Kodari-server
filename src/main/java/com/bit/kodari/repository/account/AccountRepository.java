package com.bit.kodari.repository.account;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.secret.Secret;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.repository.account.AccountSql;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.EMPTY_JWT;
import static com.bit.kodari.config.BaseResponseStatus.INVALID_JWT;

@Repository
public class AccountRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    AccountSql accountSql;
    public AccountRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    // Account 계좌 등록
    public AccountDto.PostAccountRes insert(AccountDto.PostAccountReq account) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("accountName", account.getAccountName())
                .addValue("userIdx", account.getUserIdx())
                .addValue("marketIdx", account.getMarketIdx())
                .addValue("property", account.getProperty());
        namedParameterJdbcTemplate.update(accountSql.INSERT, parameterSource, keyHolder);
        return AccountDto.PostAccountRes.builder().accountName(account.getAccountName()).build();
    }

    //유저 계좌 조회
    public List<AccountDto.GetAccountRes> getAccountByUserIdx(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        List<AccountDto.GetAccountRes> getAccountRes = namedParameterJdbcTemplate.query(AccountSql.FIND_USER_ACCOUNT, parameterSource,
                (rs, rowNum) -> new AccountDto.GetAccountRes(
                        rs.getInt("accountIdx"),
                        rs.getString("accountName"),
                        rs.getInt("userIdx"),
                        rs.getInt("marketIdx"),
                        rs.getString("property"),
                        rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getAccountRes;
    }

    //현금 자산 조회
    public List<AccountDto.GetPropertyRes> getProperty(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        List<AccountDto.GetPropertyRes> getPropertyRes = namedParameterJdbcTemplate.query(AccountSql.FIND_PROPERTY, parameterSource,
                (rs, rowNum) -> new AccountDto.GetPropertyRes(
                        rs.getInt("accountIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("property"),
                        rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getPropertyRes;
    }

    //계좌 이름 수정
    public int modifyAccountName(AccountDto.PatchAccountNameReq patchAccountNameReq) {
        String qry = AccountSql.UPDATE_ACCOUNT_NAME;
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", patchAccountNameReq.getAccountIdx())
                .addValue("accountName", patchAccountNameReq.getAccountName())
                .addValue("accountIdx", patchAccountNameReq.getAccountIdx());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //현금 자산 수정
    public int modifyProperty(AccountDto.PatchPropertyReq patchPropertyReq) {
        String qry = AccountSql.UPDATE_PROPERTY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", patchPropertyReq.getAccountIdx())
                .addValue("accountIdx", patchPropertyReq.getAccountIdx())
                .addValue("property", patchPropertyReq.getProperty());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //계좌 삭제
    public int deleteByName(AccountDto.PatchAccountDelReq patchAccountDelReq) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", patchAccountDelReq.getAccountIdx());
        return namedParameterJdbcTemplate.update(AccountSql.DELETE, parameterSource);
    }
/*
    // accountIdx로 accountDTO 반환
    public List<Room.Info> findByName(String name) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("name", "%"+name+"%");
        return namedParameterJdbcTemplate.query(RoomSql.FIND_BY_NAME,parameterSource, new roomMapper());
    }
*/
    // accountIdx로 userIdx 가져오기
    public int getUserIdxByAccountIdx(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    // accountIdx로 marketIdx 가져오기
    public int getMarketIdxByAccountIdx(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_MARKET_IDX, parameterSource, rs -> {
            int marketIdx = 0;
            if (rs.next()) {
                marketIdx = rs.getInt("marketIdx");
            }

            return marketIdx;
        });
    }

    // userIdx, marketIdx 로 accountName 전달
    public List<AccountDto.GetAccountNameRes> getAccountNameByIdx(int userIdx, int marketIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("marketIdx", marketIdx);
        try {
            List<AccountDto.GetAccountNameRes> getAccountNameRes =  namedParameterJdbcTemplate.query(AccountSql.GET_ACCOUNT_NAME, parameterSource,
                    (rs, rowNum) -> new AccountDto.GetAccountNameRes(
                            rs.getString("accountName"))
            );
            return getAccountNameRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    // accountIdx로 status 가져오기
    public String getStatusByAccountIdx(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_ACCOUNT_STATUS, parameterSource, rs -> {
            String status = "";
            if (rs.next()) {
                status = rs.getString("status");
            }

            return status;
        });
    }



}
