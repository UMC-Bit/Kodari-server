package com.bit.kodari.repository.usercoin;

import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.repository.account.AccountSql;
import com.bit.kodari.repository.portfolio.PortfolioSql;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserCoinRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    UserCoinSql userCoinSql;
    public UserCoinRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * userIdx, coinIdx 받아서 -> requestbody(priceAvg, amount)
     */
    //소유 코인 등록
    public UserCoinDto.PostUserCoinRes insert(UserCoinDto.PostUserCoinReq userCoin) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", userCoin.getUserIdx())
                .addValue("coinIdx", userCoin.getCoinIdx())
                .addValue("accountIdx", userCoin.getAccountIdx())
                .addValue("priceAvg", userCoin.getPriceAvg())
                .addValue("amount", userCoin.getAmount());
        namedParameterJdbcTemplate.update(userCoinSql.INSERT, parameterSource, keyHolder);
        int pk = keyHolder.getKey().intValue();
        return UserCoinDto.PostUserCoinRes.builder().userCoinIdx(pk).coinIdx(userCoin.getCoinIdx()).build();
    }

    //특정 소유 코인 조회
    public List<UserCoinDto.GetUserCoinIdxRes> getUserCoinByUserCoinIdx(int userCoinIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx);
        List<UserCoinDto.GetUserCoinIdxRes> getUserCoinIdxRes = namedParameterJdbcTemplate.query(UserCoinSql.FIND_USER_COIN_IDX, parameterSource,
                (rs, rowNum) -> new UserCoinDto.GetUserCoinIdxRes(
                        rs.getString("coinName"),
                        rs.getString("symbol"),
                        rs.getInt("userIdx"),
                        rs.getDouble("priceAvg"),
                        rs.getDouble("amount"),
                        rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getUserCoinIdxRes;
    }

    //소유 코인 조회
    public List<UserCoinDto.GetUserCoinRes> getUserCoinByUserIdx(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        List<UserCoinDto.GetUserCoinRes> getUserCoinRes = namedParameterJdbcTemplate.query(UserCoinSql.FIND_USER_COIN, parameterSource,
                (rs, rowNum) -> new UserCoinDto.GetUserCoinRes(
                        rs.getString("coinName"),
                        rs.getString("symbol"),
                        rs.getInt("userIdx"),
                        rs.getDouble("priceAvg"),
                        rs.getDouble("amount"),
                        rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getUserCoinRes;
    }

    //소유 코인 수정
    public int updateUserCoin(UserCoinDto.PatchUserCoinReq patchUserCoinReq) {
        String qry = UserCoinSql.UPDATE_USER_COIN;
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", patchUserCoinReq.getUserCoinIdx())
                .addValue("priceAvg", patchUserCoinReq.getPriceAvg())
                .addValue("amount", patchUserCoinReq.getAmount());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //소유 코인 삭제
    public int deleteByUserCoinIdx(int userCoinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx);
        return namedParameterJdbcTemplate.update(UserCoinSql.DELETE, parameterSource);
    }

    //소유 코인 전체 삭제(userIdx)
    public int deletebyUserIdx(UserCoinDto.PatchDelByUserIdxReq patchDelByUserIdxReq) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", patchDelByUserIdxReq.getUserIdx());
        return namedParameterJdbcTemplate.update(UserCoinSql.ALL_DELETE, parameterSource);
    }

    //매수평단가 계산
    /**
     * patch
     */
    public int updatePriceAvg(int userCoinIdx, double priceAvg, double amount) {
        String qry = UserCoinSql.PRICE_AVERAGE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx)
                .addValue("priceAvg", priceAvg)
                .addValue("amount", amount);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }


    /**
     *가져오기 위한 repository
     */
    // userCoinIdx로 userIdx 가져오기
    public int getUserIdxByUserCoinIdx(int userCoinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    // userCoinIdx로 accountIdx 가져오기
    public int getAccountIdxByUserCoinIdx(int userCoinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_ACCOUNT_IDX, parameterSource, rs -> {
            int accountIdx = 0;
            if (rs.next()) {
                accountIdx = rs.getInt("accountIdx");
            }

            return accountIdx;
        });
    }

    //accountIdx로 계좌 status 가져오기
    public String getAccountStatus(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_ACCOUNT_STATUS, parameterSource, rs -> {
            String status = "";
            if (rs.next()) {
                status = rs.getString("status");
            }

            return status;
        });
    }

    /**
     * 매수평단가 계산에 필요함
     */
    // userCoinIdx로 coinIdx 가져오기
    public int getCoinIdxByUserCoinIdx(int userCoinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_COIN_IDX, parameterSource, rs -> {
            int coinIdx = 0;
            if (rs.next()) {
                coinIdx = rs.getInt("coinIdx");
            }

            return coinIdx;
        });
    }

    // accountIdx로 portIdx 가져오기
    public int getPortIdx(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_PORT_IDX, parameterSource, rs -> {
            int portIdx = 0;
            if (rs.next()) {
                portIdx = rs.getInt("portIdx");
            }

            return portIdx;
        });
    }

    // coinIdx, portIdx로 trade 테이블의 tradeIdx 가져오기
    public int getTradeIdx(int coinIdx, int portIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("coinIdx", coinIdx)
                .addValue("portIdx", portIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_TRADE_IDX, parameterSource, rs -> {
            int tradeIdx = 0;
            if (rs.next()) {
                tradeIdx = rs.getInt("tradeIdx");
            }

            return tradeIdx;
        });
    }

    //tradeIdx로 trade 테이블의 category 가져오기
    public String getCategory(int tradeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_TRADE_CATEGORY, parameterSource, rs -> {
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
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_TRADE_PRICE, parameterSource, rs -> {
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
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_TRADE_AMOUNT, parameterSource, rs -> {
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
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_TRADE_FEE, parameterSource, rs -> {
            double fee = 0;
            if (rs.next()) {
                fee = rs.getDouble("fee");
            }

            return fee;
        });
    }

    //userCoinIdx로 priceAvg 가져오기
    public double getPriceAvg(int userCoinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_PRICE_AVG, parameterSource, rs -> {
            double priceAvg = 0;
            if (rs.next()) {
                priceAvg = rs.getDouble("priceAvg");
            }

            return priceAvg;
        });
    }

    //userCoinIdx로 UserCoin 테이블의 amount 가져오기
    public double getAmountByUserCoinIdx(int userCoinIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_USER_COIN_AMOUNT, parameterSource, rs -> {
            double amount = 0;
            if (rs.next()) {
                amount = rs.getDouble("amount");
            }

            return amount;
        });
    }

    //accountIdx로 Account의 totalProperty 가져오기
    public double getTotalProperty(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_TOTAL_PROPERTY, parameterSource, rs -> {
            double totalProperty = 0;
            if (rs.next()) {
                totalProperty = rs.getDouble("totalProperty");
            }

            return totalProperty;
        });
    }

    //accountIdx로 Account의 property 가져오기
    public double getProperty(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_PROPERTY, parameterSource, rs -> {
            double property = 0;
            if (rs.next()) {
                property = rs.getDouble("property");
            }

            return property;
        });
    }

    //accountIdx로 계좌 userIdx 가져오기
    public int getAccountUserIdx(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(UserCoinSql.GET_ACCOUNT_USER, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }
}
