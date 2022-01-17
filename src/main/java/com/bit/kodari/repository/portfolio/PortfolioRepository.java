package com.bit.kodari.repository.portfolio;

import com.bit.kodari.dto.PortfolioDto;
import com.bit.kodari.dto.UserCoinDto;
import com.bit.kodari.repository.usercoin.UserCoinSql;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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

    //포트폴리오 삭제
    public int deleteByPortIdx(PortfolioDto.PatchPortfolioDelReq patchPortfolioDelReq) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", patchPortfolioDelReq.getPortIdx());
        return namedParameterJdbcTemplate.update(PortfolioSql.DELETE, parameterSource);
    }
}
