package com.bit.kodari.repository.portfolio;

import com.bit.kodari.dto.*;
import com.bit.kodari.repository.Represent.RepresentSql;
import com.bit.kodari.repository.account.AccountSql;
import com.bit.kodari.repository.profit.ProfitSql;
import com.bit.kodari.repository.usercoin.UserCoinRepository;
import com.bit.kodari.repository.usercoin.UserCoinSql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PortfolioRepository {

    @Autowired
    UserCoinRepository userCoinRepository;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    PortfolioSql portfolioSql;
    public PortfolioRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //포트폴리오 등록
    public PortfolioDto.PostPortfolioRes insert(PortfolioDto.PostPortfolioReq portfolio) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", portfolio.getUserIdx())
                .addValue("accountIdx", portfolio.getAccountIdx());
        namedParameterJdbcTemplate.update(portfolioSql.INSERT, parameterSource, keyHolder);
        int pk = keyHolder.getKey().intValue();
        return PortfolioDto.PostPortfolioRes.builder().portIdx(pk).userIdx(portfolio.getUserIdx()).accountIdx(portfolio.getAccountIdx()).build();
    }

    //대표코인 자동으로 넣기
    public PortfolioDto.GetRepresentRes insertRepresent(int portIdx, int coinIdx) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("portIdx", portIdx)
                .addValue("coinIdx", coinIdx);
        namedParameterJdbcTemplate.update(portfolioSql.INSERT_REPRESENT, parameterSource, keyHolder);
        return PortfolioDto.GetRepresentRes.builder().coinIdx(coinIdx).build();
    }


    //포트폴리오 조회
    //리스트로 받아오게 - 수정
    //유저코인, 대표코인, 수익률, 소득
    public PortfolioDto.GetPortfolioRes getPortfolio(int portIdx){
        int accountIdx = getAccountIdx(portIdx);
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        try {
            PortfolioDto.GetPortfolioRes getPortfolioRes = namedParameterJdbcTemplate.queryForObject(PortfolioSql.GET_PORTFOLIO, parameterSource, (rs, rowNum) -> {

                List<UserCoinDto.UserCoin> userCoinList = getUserCoin(accountIdx);
                List<RepresentDto.GetRepresentRes> representCoinList= getRepresent(portIdx);
                List<ProfitDto.GetProfitRes> profitList = getProfitByAccountIdx(accountIdx);
                    PortfolioDto.GetPortfolioRes portfolio = new PortfolioDto.GetPortfolioRes(rs.getInt("portIdx"),rs.getInt("accountIdx"),rs.getString("accountName"),
                            rs.getDouble("property"),rs.getDouble("totalProperty"), rs.getInt("userIdx") ,rs.getString("marketName"), userCoinList, representCoinList, profitList);
                    return portfolio;
            }
            );
            return getPortfolioRes;
        }catch(EmptyResultDataAccessException e) {
            return null;
        }

    }

    // 포트폴리오 Idx 리스트 받아오기 - userIdx 로
    /**
     * userIdx로 전체 포트폴리오 인덱스 가져오기
     * 가져온 포트폴리오 인덱스로 각자 조회하던거 가져와서 넣기 - 리스트로
     */
    public List<PortfolioDto.GetAllPortIdxRes> getAllPortByUserIdx(int userIdx){
        try {
            SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
            List<PortfolioDto.GetAllPortIdxRes> getAllPortByUserRes = namedParameterJdbcTemplate.query(PortfolioSql.GET_ALL_PORT_IDX, parameterSource,(rs, rowNum) ->
                    new PortfolioDto.GetAllPortIdxRes(rs.getInt("portIdx")));
            return getAllPortByUserRes;
        }catch(EmptyResultDataAccessException e) {
            return null;
        }
    }


    //포트폴리오 삭제 - 소유코인, 계좌, 대표코인 다 삭제되도록(모두 있을 때)
    public int deleteByPortIdx(int portIdx, int accountIdx, int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx)
                .addValue("accountIdx", accountIdx)
                .addValue("userIdx", userIdx);
        return namedParameterJdbcTemplate.update(PortfolioSql.DELETE, parameterSource);
    }

    //포트폴리오 삭제 - 계좌, 대표코인 다 삭제되도록(소유 코인 없을 때)
    public int deleteTwo(int portIdx, int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx)
                .addValue("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.update(PortfolioSql.DELETE_TWO, parameterSource);
    }

    // 대표코인 삭제
    public int deleteRepresent(int portIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        return namedParameterJdbcTemplate.update(PortfolioSql.DELETE_REPRESENT, parameterSource);
    }


    // 포트폴리오 삭제 : 전체 삭제
    public int deleteAllPortfolioByUserIdx(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);

        return namedParameterJdbcTemplate.update(PortfolioSql.DELETE_ALL, parameterSource);
    }


    /**
     * 가져오기
     */
    //accountIdx로 계좌 status 가져오기
    public String getAccountStatus(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(PortfolioSql.GET_ACCOUNT_STATUS, parameterSource, rs -> {
            String status = "";
            if (rs.next()) {
                status = rs.getString("status");
            }

            return status;
        });
    }

    // portIdx로 userIdx 가져오기
    public int getUserIdxByPortIdx(int portIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        return namedParameterJdbcTemplate.query(PortfolioSql.GET_USER_IDX, parameterSource, rs -> {
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
        return namedParameterJdbcTemplate.query(PortfolioSql.GET_ACCOUNT_IDX, parameterSource, rs -> {
            int accountIdx = 0;
            if (rs.next()) {
                accountIdx = rs.getInt("accountIdx");
            }

            return accountIdx;
        });
    }

    // 모든 포트폴리오 가져오기 - List
    public List<PortfolioDto.GetAllPortfolioRes> getAllPortfolio(){
        try {
            List<PortfolioDto.GetAllPortfolioRes> getAllPortfolioRes =  namedParameterJdbcTemplate.query(PortfolioSql.GET_ALL_PORTFOLIO,
                    (rs, rowNum) -> new PortfolioDto.GetAllPortfolioRes(
                            rs.getInt("userIdx"),
                            rs.getInt("accountIdx"))
            );
            return getAllPortfolioRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    // userIdx로 모든 portIdx 가져오기 - List
    public List<PortfolioDto.GetAllPortIdxRes> getAllPortIdx(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        try {
            List<PortfolioDto.GetAllPortIdxRes> getAllPortIdxRes =  namedParameterJdbcTemplate.query(PortfolioSql.GET_ALL_PORT_IDX, parameterSource,
                    (rs, rowNum) -> new PortfolioDto.GetAllPortIdxRes(
                            rs.getInt("portIdx"))
            );
            return getAllPortIdxRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    // accountIdx로 userCoinIdx 가져오기 - 리스트
    public List<UserCoinDto.UserCoin> getUserCoin(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        try {
            List<UserCoinDto.UserCoin> userCoinList =  namedParameterJdbcTemplate.query(PortfolioSql.GET_USER_COIN, parameterSource,
                    (rs, rowNum) -> new UserCoinDto.UserCoin(
                            rs.getInt("userCoinIdx"),
                            rs.getInt("userIdx"),
                            rs.getInt("coinIdx"),
                            rs.getString("coinName"),
                            rs.getString("symbol"),
                            rs.getString("coinImg"),
                            rs.getInt("accountIdx"),
                            rs.getDouble("priceAvg"),
                            rs.getDouble("amount"),
                            rs.getString("status"))
            );
            return userCoinList;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    // 대표 코인 조회 - userIdx
    public List<RepresentDto.GetRepresentRes> getRepresent(int portIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        List<RepresentDto.GetRepresentRes> getRepresentRes = namedParameterJdbcTemplate.query(RepresentSql.FIND_USER_REPRESENT, parameterSource,
                (rs, rowNum) -> new RepresentDto.GetRepresentRes(
                        rs.getInt("representIdx"),
                        rs.getInt("portIdx"),
                        rs.getInt("coinIdx"),
                        rs.getString("coinName"),
                        rs.getString("symbol"),
                        rs.getString("coinImg"),
                        rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );
        return getRepresentRes;
    }

    // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
    public List<ProfitDto.GetProfitRes> getProfitByAccountIdx(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        List<ProfitDto.GetProfitRes> getProfitRes = namedParameterJdbcTemplate.query(ProfitSql.FIND_BY_ACCOUNTIDX, parameterSource,
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

    // accountIdx로 모든 userCoinIdx 가져오기 - List
    public List<PortfolioDto.GetUserCoinIdxRes> getUserCoinIdx(int accountIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        try {
            List<PortfolioDto.GetUserCoinIdxRes> getUserCoinIdxRes =  namedParameterJdbcTemplate.query(PortfolioSql.GET_USER_COIN_IDX, parameterSource,
                    (rs, rowNum) -> new PortfolioDto.GetUserCoinIdxRes(
                            rs.getInt("userCoinIdx"))
            );
            return getUserCoinIdxRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //accountIdx로 계좌 userIdx 가져오기
    public int getAccountUserIdx(int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.query(PortfolioSql.GET_ACCOUNT_USER, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

}
