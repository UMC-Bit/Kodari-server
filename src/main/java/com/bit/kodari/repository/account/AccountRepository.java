package com.bit.kodari.repository.account;

import com.bit.kodari.config.BaseException;
//import com.bit.kodari.config.secret.Secret;
import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.PortfolioDto;
import com.bit.kodari.repository.account.AccountSql;
import com.bit.kodari.repository.portfolio.PortfolioSql;
import com.bit.kodari.repository.trade.TradeSql;
import com.bit.kodari.repository.usercoin.UserCoinSql;
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
        int pk = keyHolder.getKey().intValue();
        return AccountDto.PostAccountRes.builder().accountIdx(pk).accountName(account.getAccountName()).build();
    }

    // Account 총자산 수정
    public int modifyTotal(int accountIdx, double totalProperty) {
        String qry = AccountSql.UPDATE_TOTAL_PROPERTY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx)
                .addValue("accountIdx", accountIdx)
                .addValue("totalProperty", totalProperty);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
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
                        rs.getDouble("totalProperty"),
                        rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getAccountRes;
    }

    //유저 계좌 단일 조회
    public List<AccountDto.GetAccountByAccountIdxRes> getAccountByAccountIdx(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        List<AccountDto.GetAccountByAccountIdxRes> getAccountByAccountIdxRes = namedParameterJdbcTemplate.query(AccountSql.FIND_ACCOUNT_BY_ACCOUNT_IDX, parameterSource,
                (rs, rowNum) -> new AccountDto.GetAccountByAccountIdxRes(
                        rs.getString("accountName"),
                        rs.getInt("marketIdx"),
                        rs.getString("property"),
                        rs.getDouble("totalProperty"),
                        rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getAccountByAccountIdxRes;
    }

    //현금 자산 조회
    public List<AccountDto.GetPropertyRes> getProperty(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        List<AccountDto.GetPropertyRes> getPropertyRes = namedParameterJdbcTemplate.query(AccountSql.FIND_PROPERTY, parameterSource,
                (rs, rowNum) -> new AccountDto.GetPropertyRes(
                        rs.getInt("accountIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("property"),
                        rs.getDouble("totalProperty"),
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
    public int modifyProperty(AccountDto.PatchPropertyReq patchPropertyReq, double totalProperty) {
        String qry = AccountSql.UPDATE_PROPERTY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", patchPropertyReq.getAccountIdx())
                .addValue("accountIdx", patchPropertyReq.getAccountIdx())
                .addValue("property", patchPropertyReq.getProperty())
                .addValue("totalProperty", totalProperty);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    // Trade - 현금 자산 수정
    public int modifyTradeProperty(double property, int accountIdx) {
        String qry = AccountSql.UPDATE_TRADE_PROPERTY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("property", property)
                .addValue("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //계좌 삭제
    public int deleteByIdx(AccountDto.PatchAccountDelReq patchAccountDelReq) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", patchAccountDelReq.getAccountIdx());
        return namedParameterJdbcTemplate.update(AccountSql.DELETE, parameterSource);
    }

    //계좌 삭제 - 계좌, 포트폴리오만
    public int deleteTwo(AccountDto.PatchAccountDelReq patchAccountDelReq) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", patchAccountDelReq.getAccountIdx());
        return namedParameterJdbcTemplate.update(AccountSql.DELETE_TWO, parameterSource);
    }

    // 총 자산 수정 - 업데이트 버튼 누를때마다
    public int updateTotalProperty(int accountIdx, double totalProperty) {
        String qry = AccountSql.UPDATE_TOTAL_PROPERTY_BUTTON;
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx)
                .addValue("totalProperty", totalProperty);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }


    // 거래내역 삭제 : 전체 거래내역 삭제
    public int deleteAllAccountByUserIdx(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);

        return namedParameterJdbcTemplate.update(AccountSql.DELETE_ALL, parameterSource);
    }

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

    // userIdx, accountIdx로 userCoin 전달
    public List<AccountDto.GetUserCoinRes> getUserCoinByIdx(int userIdx, int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("accountIdx", accountIdx);
        try {
            List<AccountDto.GetUserCoinRes> getUserCoinRes =  namedParameterJdbcTemplate.query(AccountSql.GET_USER_COIN, parameterSource,
                    (rs, rowNum) -> new AccountDto.GetUserCoinRes(
                            rs.getDouble("priceAvg"),
                            rs.getDouble("amount"))
            );
            return getUserCoinRes;

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

    // accountIdx로 accountName 가져오기
    public String getAccountNameByAccountIdx(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_NAME_BY_ACCOUNT_IDX, parameterSource, rs -> {
            String accountName = "";
            if (rs.next()) {
                accountName = rs.getString("accountName");
            }

            return accountName;
        });
    }

    // accountIdx로 property 가져오기
    public double getPropertyByAccount(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_PROPERTY, parameterSource, rs -> {
            double property = 0;
            if (rs.next()) {
                property = rs.getInt("property");
            }

            return property;
        });
    }

    // accountIdx로 marketIdx 가져오기
    public int getMarketIdxByAccount(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_MARKET_IDX_BY_ACCOUNT, parameterSource, rs -> {
            int marketIdx = 0;
            if (rs.next()) {
                marketIdx = rs.getInt("marketIdx");
            }

            return marketIdx;
        });
    }

    // accountIdx로 totalProperty 가져오기
    public double getTotalPropertyByAccount(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_TOTAL_BY_ACCOUNT_IDX, parameterSource, rs -> {
            double totalProperty = 0;
            if (rs.next()) {
                totalProperty = rs.getInt("totalProperty");
            }

            return totalProperty;
        });
    }

    // tradeIdx로 portIdx 가져오기
    public int getPortIdx(int tradeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_PORT_IDX, parameterSource, rs -> {
            int portIdx = 0;
            if (rs.next()) {
                portIdx = rs.getInt("portIdx");
            }

            return portIdx;
        });
    }

    // tradeIdx로 coinIdx 가져오기
    public int getCoinIdx(int tradeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_COIN_IDX, parameterSource, rs -> {
            int coinIdx = 0;
            if (rs.next()) {
                coinIdx = rs.getInt("coinIdx");
            }

            return coinIdx;
        });
    }

    // portIdx로 userIdx 가져오기
    public int getUserIdxByPort(int portIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_USER_IDX_BY_PORT, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    // portIdx로 accountIdx 가져오기
    public int getAccountIdx(int portIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_ACCOUNT_IDX, parameterSource, rs -> {
            int accountIdx = 0;
            if (rs.next()) {
                accountIdx = rs.getInt("accountIdx");
            }

            return accountIdx;
        });
    }

    //tradeIdx로 trade 테이블의 category 가져오기
    public String getCategory(int tradeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_TRADE_CATEGORY, parameterSource, rs -> {
            String category = "";
            if (rs.next()) {
                category = rs.getString("category");
            }

            return category;
        });
    }

    //tradeIdx로 trade 테이블의 price 가져오기
    public double getPrice(int tradeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_TRADE_PRICE, parameterSource, rs -> {
            double price = 0;
            if (rs.next()) {
                price = rs.getDouble("price");
            }

            return price;
        });
    }

    //tradeIdx로 trade 테이블의 amount 가져오기
    public double getAmount(int tradeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_TRADE_AMOUNT, parameterSource, rs -> {
            double amount = 0;
            if (rs.next()) {
                amount = rs.getDouble("amount");
            }

            return amount;
        });
    }

    //tradeIdx로 trade 테이블의 fee 가져오기
    public double getFee(int tradeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_TRADE_FEE, parameterSource, rs -> {
            double fee = 0;
            if (rs.next()) {
                fee = rs.getDouble("fee");
            }

            return fee;
        });
    }

    // userIdx, marketIdx 로 accountIdx 전달
    public List<AccountDto.GetAccountIdxRes> getAccountIdxByIdx(int userIdx, int marketIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("marketIdx", marketIdx);
        try {
            List<AccountDto.GetAccountIdxRes> getAccountIdxRes =  namedParameterJdbcTemplate.query(AccountSql.GET_ACCOUNT_IDX_THREE, parameterSource,
                    (rs, rowNum) -> new AccountDto.GetAccountIdxRes(
                            rs.getInt("accountIdx"))
            );
            return getAccountIdxRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    // accountIdx로 모든 userCoinIdx 가져오기 - List
    public List<AccountDto.GetUserCoinIdxRes> getUserCoinIdx(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        try {
            List<AccountDto.GetUserCoinIdxRes> getUserCoinIdxRes =  namedParameterJdbcTemplate.query(AccountSql.GET_USER_COIN_IDX_LIST, parameterSource,
                    (rs, rowNum) -> new AccountDto.GetUserCoinIdxRes(
                            rs.getInt("userCoinIdx"))
            );
            return getUserCoinIdxRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }


    public String getCreateAtByAccountIdx(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx",accountIdx);
        return namedParameterJdbcTemplate.query(AccountSql.GET_CREATEAT, parameterSource, rs -> {
            String createAt = "";
            if (rs.next()) {
                createAt = rs.getString("createAt");
            }

            return createAt;
        });
    }

}
