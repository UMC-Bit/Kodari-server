package com.bit.kodari.repository.portfolio;

import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.PortfolioDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.repository.account.AccountSql;
import com.bit.kodari.repository.usercoin.UserCoinSql;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PortfolioRepository {

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


    //포트폴리오 조회
    //리스트로 받아오게 - 수정
    public List<PortfolioDto.GetPortfolioRes> getPortfolio(int portIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        List<PortfolioDto.GetPortfolioRes> getPortfolioRes = namedParameterJdbcTemplate.query(PortfolioSql.GET_PORTFOLIO, parameterSource,
                (rs, rowNum) -> new PortfolioDto.GetPortfolioRes(
                        rs.getInt("portIdx"),
                        rs.getInt("accountIdx"),
                        rs.getString("accountName"),
                        rs.getDouble("property"),
                        rs.getInt("userIdx"),
                        rs.getString("marketName"),
                        rs.getInt("userCoinIdx"),
                        rs.getString("coinName"),
                        rs.getString("coinImg"),
                        rs.getDouble("priceAvg"),
                        rs.getDouble("amount")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getPortfolioRes;
    }

    //포트폴리오 삭제 - 소유코인, 계좌 다 삭제되도록
    public int deleteByPortIdx(int portIdx, int accountIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx)
                .addValue("accountIdx", accountIdx);
        return namedParameterJdbcTemplate.update(PortfolioSql.DELETE, parameterSource);
    }

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

}
