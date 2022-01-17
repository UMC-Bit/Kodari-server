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
        return PortfolioDto.PostPortfolioRes.builder().accountIdx(portfolio.getAccountIdx()).build();
    }

    /**
    //포트폴리오 조회
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
*/
    //포트폴리오 삭제
    public int deleteByPortIdx(PortfolioDto.PatchPortfolioDelReq patchPortfolioDelReq) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", patchPortfolioDelReq.getPortIdx());
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
