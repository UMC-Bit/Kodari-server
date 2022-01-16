package com.bit.kodari.repository.trade;

import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.dto.UserDto;
import com.bit.kodari.repository.user.UserSql;
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
    private JdbcTemplate jdbcTemplate;
    TradeSql tradeSql; // sql문 모아놓은 클래스
    // 생성자 의존주입
    public TradeRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 거래내역 생성:  레코드 추가
    public int createTrade(TradeDto.PostTradeReq postTradeReq){
        // TradeReq 레코드 추가
        //KeyHolder keyHolder = new GeneratedKeyHolder();
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", postTradeReq.getPortIdx())
                .addValue("coinIdx", postTradeReq.getCoinIdx())
                .addValue("price", postTradeReq.getPrice())
                .addValue("amount", postTradeReq.getAmount())
                .addValue("fee", postTradeReq.getFee())
                .addValue("category", postTradeReq.getCategory())
                .addValue("memo", postTradeReq.getMemo());

        int affectedRows = namedParameterJdbcTemplate.update(TradeSql.INSERT,parameterSource,keyHolder);

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        //int lastInsertIdx = namedParameterJdbcTemplate.getJdbcTemplate().queryForObject(TradeSql.FIND_LAST_INSERT_ID,int.class);
        int lastInsertIdx = namedParameterJdbcTemplate.getJdbcOperations().queryForObject(TradeSql.FIND_LAST_INSERT_ID,int.class);
        return lastInsertIdx;
        // 추가된 정보를 postUserRes 형태로 반환
        //String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        //int lastInsertIdx = namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_LAST_INSERT_ID ,int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
        //"SELECT count(tradeIdx) FROM Trade"
        //int lastInsertIdx = keyHolder.getKey().intValue();
//        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
//
//        int lastInsertIdx = this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
        /*SqlParameterSource parameterSource1 = new MapSqlParameterSource("tradeIdx",lastInsertIdx);
        TradeDto.PostTradeRes postTradeRes = namedParameterJdbcTemplate.queryForObject(TradeSql.FIND_BY_TRADEIDX,parameterSource1,
                (rs,rowNum) -> new TradeDto.PostTradeRes(
                        rs.getInt("tradeIdx"),
                        rs.getInt("portIdx"),
                        rs.getInt("coinIdx")
                ));
        return postTradeRes;*/

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



    // 거래내역 삭제 : status 수정
    public int deleteTrade(TradeDto.PatchStatusReq patchStatusReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("tradeIdx", patchStatusReq.getTradeIdx());

        return namedParameterJdbcTemplate.update(TradeSql.DELETE, parameterSource);
    }

}
