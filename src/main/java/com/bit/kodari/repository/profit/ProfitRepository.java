package com.bit.kodari.repository.profit;

import com.bit.kodari.dto.ProfitDto;
import com.bit.kodari.dto.TradeDto;
import com.bit.kodari.repository.trade.TradeSql;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfitRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    ProfitSql profitSql; // sql문 모아놓은 클래스
    // 생성자 의존주입
    public ProfitRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // 수익내역 생성:  레코드 추가
    public ProfitDto.PostProfitRes createProfit(ProfitDto.PostProfitReq postProfitReq){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", postProfitReq.getAccountIdx())
                .addValue("profitRate",postProfitReq.getProfitRate())
                .addValue("earning",postProfitReq.getEarning())
                .addValue("date",postProfitReq.getDate());

        int affectedRows = namedParameterJdbcTemplate.update(ProfitSql.INSERT,parameterSource,keyHolder);


        // 해당 profitIdx 의 거래내역을 PostProfitRes 형태로 반환
        int lastInsertIdx = keyHolder.getKey().intValue(); // 마지막 들어간 idx 조회
        SqlParameterSource parameterSource1 = new MapSqlParameterSource("profitIdx",lastInsertIdx);
        ProfitDto.PostProfitRes postProfitRes = namedParameterJdbcTemplate.queryForObject(ProfitSql.FIND_BY_PROFITIDX,parameterSource1,
                (rs,rowNum) -> new ProfitDto.PostProfitRes(
                        rs.getInt("profitIdx"),
//                        rs.getInt("portIdx"),
                        rs.getInt("accountIdx"),
                        rs.getDouble("profitRate"),
                        rs.getDouble("earning"),
                        rs.getString("status")
                ));
        return postProfitRes;
    }



    // 수익내역 생성:  과거 레코드 추가
    public ProfitDto.PostProfitRes createPrevProfit(ProfitDto.PostPrevProfitReq postPrevProfitReq){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", postPrevProfitReq.getAccountIdx())
                .addValue("profitRate",postPrevProfitReq.getProfitRate())
                .addValue("earning",postPrevProfitReq.getEarning())
                .addValue("prevDate",postPrevProfitReq.getPrevDate());

        int affectedRows = namedParameterJdbcTemplate.update(ProfitSql.INSERT_PREV,parameterSource,keyHolder);


        // 해당 profitIdx 의 거래내역을 PostProfitRes 형태로 반환
        int lastInsertIdx = keyHolder.getKey().intValue(); // 마지막 들어간 idx 조회
        SqlParameterSource parameterSource1 = new MapSqlParameterSource("profitIdx",lastInsertIdx);
        ProfitDto.PostProfitRes postProfitRes = namedParameterJdbcTemplate.queryForObject(ProfitSql.FIND_BY_PROFITIDX,parameterSource1,
                (rs,rowNum) -> new ProfitDto.PostProfitRes(
                        rs.getInt("profitIdx"),
//                        rs.getInt("portIdx"),
                        rs.getInt("accountIdx"),
                        rs.getDouble("profitRate"),
                        rs.getDouble("earning"),
                        rs.getString("status")
                ));
        return postProfitRes;
    }






    // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
    public List<ProfitDto.GetProfitRes> getProfitByAccountIdx(ProfitDto.GetProfitReq getProfitReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", getProfitReq.getAccountIdx());

        try{
            // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
            List<ProfitDto.GetProfitRes> getProfitRes = namedParameterJdbcTemplate.query(ProfitSql.FIND_BY_ACCOUNTIDX, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new ProfitDto.GetProfitRes(
                            rs.getInt("profitIdx"),
                            rs.getInt("accountIdx"),
                            rs.getDouble("profitRate"),
                            rs.getString("earning"),
                            rs.getString("status"),
                            rs.getString("createAt")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getProfitRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }



    // Profit 수익내역 일별 조회: 특정 계좌의 수익내역 조회
    public List<ProfitDto.GetProfitRes> getDailyProfitByAccountIdx(ProfitDto.GetProfitReq getProfitReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", getProfitReq.getAccountIdx());

        try{
            // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
            List<ProfitDto.GetProfitRes> getProfitRes = namedParameterJdbcTemplate.query(ProfitSql.FIND_DAILY_BY_ACCOUNTIDX, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new ProfitDto.GetProfitRes(
                            rs.getInt("profitIdx"),
                            rs.getInt("accountIdx"),
                            rs.getDouble("profitRate"),
                            rs.getString("earning"),
                            rs.getString("status"),
                            rs.getString("createAt")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getProfitRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }



    // Profit 수익내역 주 별 조회: 특정 계좌의 전체 수익내역 조회
    public List<ProfitDto.GetProfitRes> getWeeklyProfitByAccountIdx(ProfitDto.GetProfitReq getProfitReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", getProfitReq.getAccountIdx());

        try{
            // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
            List<ProfitDto.GetProfitRes> getProfitRes = namedParameterJdbcTemplate.query(ProfitSql.FIND_WEEKLY_BY_ACCOUNTIDX, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new ProfitDto.GetProfitRes(
                            rs.getInt("profitIdx"),
                            rs.getInt("accountIdx"),
                            rs.getDouble("profitRate"),
                            rs.getString("earning"),
                            rs.getString("status"),
                            rs.getString("createAt")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getProfitRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }



    // Profit 수익내역 월 별 조회: 특정 계좌의 전체 수익내역 조회
    public List<ProfitDto.GetProfitRes> getMonthlyProfitByAccountIdx(ProfitDto.GetProfitReq getProfitReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", getProfitReq.getAccountIdx());

        try{
            // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
            List<ProfitDto.GetProfitRes> getProfitRes = namedParameterJdbcTemplate.query(ProfitSql.FIND_MONTHLY_BY_ACCOUNTIDX, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new ProfitDto.GetProfitRes(
                            rs.getInt("profitIdx"),
                            rs.getInt("accountIdx"),
                            rs.getDouble("profitRate"),
                            rs.getString("earning"),
                            rs.getString("status"),
                            rs.getString("createAt")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getProfitRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }




    // Profit 수익내역 조회: 특정 계좌의 코인심볼 전체조회
    public List<ProfitDto.GetCoinSymbolRes> getSymbolByAccountIdx(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);

        try{
            // Trade 거래내역 조회: 특정 포트폴리오의  특정 코인의 전체 매수,매도 조회
            List<ProfitDto.GetCoinSymbolRes> getSymbolList = namedParameterJdbcTemplate.query(ProfitSql.FIND_SYMBOL_BY_ACCOUNTIDX, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new ProfitDto.GetCoinSymbolRes(
                            rs.getDouble("amount"),
                            rs.getDouble("priceAvg"),
                            rs.getString("symbol"),
                            rs.getDouble("property"),
                            rs.getDouble("totalProperty")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getSymbolList;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }




    //  계좌인덱스로 해당 유저인덱스 조회
    public int getUserIdxByAccountIdx(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);

        return namedParameterJdbcTemplate.queryForObject(ProfitSql.FIND_USERIDX_BY_ACCOUNTIDX,parameterSource,int.class);

        //return namedParameterJdbcTemplate.update(TradeSql.UPDATE_PRICE, parameterSource);
    }

    //  profit인덱스로 해당 유저인덱스 조회
    public int getUserIdxByProfitIdx(int profitIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("profitIdx", profitIdx);

        return namedParameterJdbcTemplate.queryForObject(ProfitSql.FIND_USERIDX_BY_PROFITIDX,parameterSource,int.class);

        //return namedParameterJdbcTemplate.update(TradeSql.UPDATE_PRICE, parameterSource);
    }

    // 수익조회: 전체 accountIdx 조회
    public List<Integer> getAllAccountIdx(){
        SqlParameterSource parameterSource = new MapSqlParameterSource();

//        namedParameterJdbcTemplate.query(ProfitSql.DELETE,int.class);
//        namedParameterJdbcTemplate.query(ProfitSql.DELETE,int.class);
        return namedParameterJdbcTemplate.queryForList(ProfitSql.FIND_ACCOUNTIDX,parameterSource,int.class);
//        return namedParameterJdbcTemplate.query(ProfitSql.FIND_ACCOUNTIDX,parameterSource,int.class);

        //return namedParameterJdbcTemplate.update(TradeSql.UPDATE_PRICE, parameterSource);
    }




    //  profit인덱스로 status 값 조회
    public String getStatusByProfitIdx(int profitIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("profitIdx", profitIdx);

        return namedParameterJdbcTemplate.queryForObject(ProfitSql.FIND_STATUS_BY_PROFITIDX,parameterSource,String.class);

        //return namedParameterJdbcTemplate.update(TradeSql.UPDATE_PRICE, parameterSource);
    }




    // Profit 수익내역 삭제: 특정 계좌의 수익내역 삭제
    public int deleteProfit(ProfitDto.PatchStatusReq patchStatusReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("profitIdx",patchStatusReq.getProfitIdx());

        return namedParameterJdbcTemplate.update(ProfitSql.DELETE,parameterSource);
    }



    // 수익내역 삭제 : 전체삭제
    public int deleteAllProfitByUserIdx(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);

        return namedParameterJdbcTemplate.update(ProfitSql.DELETE_ALL, parameterSource);
    }




    // Trade연동 수익내역 삭제 및 새로 생성
    public int deleteProfitByUserCoinIdx(int userCoinIdx,String date){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userCoinIdx",userCoinIdx)
                .addValue("date",date);

        return namedParameterJdbcTemplate.update(ProfitSql.DELETE_BY_USERCOINIDX_DATE,parameterSource);
    }







}
