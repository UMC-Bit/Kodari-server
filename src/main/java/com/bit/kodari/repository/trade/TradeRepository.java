package com.bit.kodari.repository.trade;

import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.repository.account.AccountSql;
import com.bit.kodari.repository.portfolio.PortfolioSql;
import com.bit.kodari.repository.user.UserSql;
import com.bit.kodari.repository.usercoin.UserCoinSql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class TradeRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    TradeSql tradeSql; // sql문 모아놓은 클래스
    // 생성자 의존주입
    public TradeRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    // 거래내역 생성:  레코드 추가
    public TradeDto.PostTradeRes createTrade(TradeDto.PostTradeReq postTradeReq){
        // TradeReq 레코드 추가
        //KeyHolder keyHolder = new GeneratedKeyHolder();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", postTradeReq.getPortIdx())
                .addValue("coinIdx", postTradeReq.getCoinIdx())
                .addValue("price", postTradeReq.getPrice())
                .addValue("amount", postTradeReq.getAmount())
                .addValue("fee", postTradeReq.getFee())
                .addValue("category", postTradeReq.getCategory())
                .addValue("memo", postTradeReq.getMemo())
                .addValue("date", postTradeReq.getDate());

        int affectedRows = namedParameterJdbcTemplate.update(TradeSql.INSERT,parameterSource,keyHolder);

        // 해당 tradeIdx 의 거래내역을 PostTradeRes 형태로 반환
        int lastInsertIdx = keyHolder.getKey().intValue(); // 마지막 들어간 idx 조회
        SqlParameterSource parameterSource1 = new MapSqlParameterSource("tradeIdx",lastInsertIdx);
        TradeDto.PostTradeRes postTradeRes = namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_BY_TRADEIDX,parameterSource1,
                (rs,rowNum) -> new TradeDto.PostTradeRes(
                        rs.getInt("tradeIdx")
                ));
        return postTradeRes;
    }



    // Trade 거래내역 조회: 특정 포트폴리오의  특정 코인의 전체 매수,매도 조회
    public List<TradeDto.GetTradeRes> getTradeByPortIdxCoinIdx(TradeDto.GetTradeReq getTradeReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", getTradeReq.getPortIdx())
                .addValue("coinIdx",getTradeReq.getCoinIdx());
        try{
            // Trade 거래내역 조회: 특정 포트폴리오의  특정 코인의 전체 매수,매도 조회
            List<TradeDto.GetTradeRes> getTradeRes = namedParameterJdbcTemplate.query(TradeSql.FIND_BY_PORTIDX_COINIDX, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new TradeDto.GetTradeRes(
                            rs.getInt("tradeIdx"),
                            rs.getString("coinName"),
                            rs.getDouble("price"),
                            rs.getDouble("amount"),
                            rs.getDouble("fee"),
                            rs.getString("category"),
                            rs.getString("memo"),
                            rs.getString("date"),
                            rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getTradeRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }



    // Trade 조회: 특정 거래내역 조회
    public TradeDto.Trade getTradeByTradeIdx(int tradeIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx",tradeIdx );
        try{
            // Trade 조회: 특정 거래내역 조회
            TradeDto.Trade getTradeRes = namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_BY_TRADEIDX, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new TradeDto.Trade(
                            rs.getInt("tradeIdx"),
                            rs.getInt("portIdx"),
                            rs.getInt("coinIdx"),
                            rs.getDouble("price"),
                            rs.getDouble("amount"),
                            rs.getDouble("fee"),
                            rs.getString("category"),
                            rs.getString("memo"),
                            rs.getString("date"),
                            rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getTradeRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }



    //  거래인덱스로 해당 유저인덱스 조회
    public int getUserIdxByTradeIdx(int tradeIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);

        return namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_USERIDX_BY_TRADEIDX,parameterSource,int.class);

        //return namedParameterJdbcTemplate.update(TradeSql.UPDATE_PRICE, parameterSource);
    }


    //  거래내역 생성 시 포트폴리오 인덱스로 유저 1명 조회
    public int getUserIdxByPortIdx(int portIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);


        return namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_USERIDX_BY_PORTIDX,parameterSource,int.class);

        //return namedParameterJdbcTemplate.update(TradeSql.UPDATE_PRICE, parameterSource);
    }



    // status 조회: Trade인덱스로 status 조회,
    public String getStatusByTradeIdx(int tradeIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx",tradeIdx);
        return namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_STATUS_BY_TRADEIDX,parameterSource,String.class);
    }



    // 현금자산 조회: portIdx 로 Account의 현금자산 조회
    public double getCashPropertyByPortIdx(int portIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx",portIdx);
        return namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_PROPERTY_BY_PORTIDX,parameterSource,double.class);
    }

    // 소유 코인 갯수조회: accountIdx, coinIdx 로 UserCoin의  해당 소유 코인 갯수조회
    public double getUserCoinAmountByAccountIdxCoinIdx(int accountIdx,int coinIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx",accountIdx)
                .addValue("coinIdx",coinIdx);
        return namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_USERCOIN_AMOUNT_BY_ACCOUNTIDX_COINIDX,parameterSource,double.class);
    }




    // 거래내역 수정 : 코인 가격 수정(Patch)
    public int updatePrice(TradeDto.PatchPriceReq patchPriceReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", patchPriceReq.getTradeIdx())
                .addValue("price", patchPriceReq.getPrice());

        return namedParameterJdbcTemplate.update(TradeSql.UPDATE_PRICE, parameterSource);
    }


    // 거래내역 수정 : 코인 갯수 수정(Patch)
    public int updateAmount(TradeDto.PatchAmountReq patchAmountReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", patchAmountReq.getTradeIdx())
                .addValue("amount", patchAmountReq.getAmount());

        return namedParameterJdbcTemplate.update(TradeSql.UPDATE_AMOUNT, parameterSource);
    }



    // 거래내역 수정 : 수수료 수정
    public int updateFee(TradeDto.PatchFeeReq patchFeeReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", patchFeeReq.getTradeIdx())
                .addValue("fee", patchFeeReq.getFee());

        return namedParameterJdbcTemplate.update(TradeSql.UPDATE_FEE, parameterSource);
    }



    // 거래내역 수정 : 매수/매도 수정(Patch)
    public int updateCategory(TradeDto.PatchCategoryReq patchCategoryReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", patchCategoryReq.getTradeIdx())
                .addValue("category", patchCategoryReq.getCategory());

        return namedParameterJdbcTemplate.update(TradeSql.UPDATE_CATEGORY, parameterSource);
    }


    // 거래내역 수정 : 메모 수정(Patch)
    public int updateMemo(TradeDto.PatchMemoReq patchMemoReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", patchMemoReq.getTradeIdx())
                .addValue("memo", patchMemoReq.getMemo());

        return namedParameterJdbcTemplate.update(TradeSql.UPDATE_MEMO, parameterSource);
    }



    // 거래내역 수정 : 거래시각 수정(Patch)
    public int updateDate(TradeDto.PatchDateReq patchDateReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", patchDateReq.getTradeIdx())
                .addValue("date", patchDateReq.getDate());

        return namedParameterJdbcTemplate.update(TradeSql.UPDATE_DATE, parameterSource);
    }

    // 거래내역 수정 : 가격 수정시 - 매수평단가 수정
    public int updatePriceAvg(int userCoinIdx, double priceAvg) {
        String qry = TradeSql.PRICE_AVERAGE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx)
                .addValue("priceAvg", priceAvg);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    // 거래내역 수정 : 갯수 수정시 - 매수평단가, 코인갯수 수정
    public int updateUserCoinInfo(int userCoinIdx, double priceAvg, double amount) {
        String qry = TradeSql.AVG_AMOUNT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx", userCoinIdx)
                .addValue("priceAvg", priceAvg)
                .addValue("amount", amount);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    // 거래내역 수정 : 현금 자산, 총자산 수정
    public int modifyProperty(double property, double totalProperty, int tradeIdx) {
        String qry = TradeSql.UPDATE_PROPERTY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("property", property)
                .addValue("totalProperty", totalProperty)
                .addValue("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    // 거래내역 삭제 : status 수정
    public int deleteTrade(TradeDto.PatchStatusReq patchStatusReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", patchStatusReq.getTradeIdx());

        return namedParameterJdbcTemplate.update(TradeSql.DELETE, parameterSource);
    }



    // 거래내역 삭제 : 전체 거래내역 삭제
    public int deleteAllTradeByUserIdx(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);

        return namedParameterJdbcTemplate.update(TradeSql.DELETE_ALL, parameterSource);
    }

    // tradeIdx로  가져오기 - 리스트
    public List<TradeDto.GetTradeInfoRes> getTradeInfo(int tradeIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        try {
            List<TradeDto.GetTradeInfoRes> getTradeInfoRes =  namedParameterJdbcTemplate.query(TradeSql.PATCH_TRADE, parameterSource,
                    (rs, rowNum) -> new TradeDto.GetTradeInfoRes(
                            rs.getDouble("price"),
                            rs.getDouble("amount"),
                            rs.getDouble("fee"),
                            rs.getString("category"),
                            rs.getDouble("property"),
                            rs.getDouble("totalProperty"),
                            rs.getDouble("priceAvg"),
                            rs.getDouble("uc_amount"))
            );
            return getTradeInfoRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    // tradeIdx로 userCoinIdx 가져오기
    public int getUserCoinIdxByTradeIdx(int tradeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", tradeIdx);
        return namedParameterJdbcTemplate.query(TradeSql.GET_USER_COIN_IDX, parameterSource, rs -> {
            int userCoinIdx = 0;
            if (rs.next()) {
                userCoinIdx = rs.getInt("userCoinIdx");
            }

            return userCoinIdx;
        });
    }

}
