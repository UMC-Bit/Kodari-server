package com.bit.kodari.repository.Represent;

import com.bit.kodari.dto.AccountDto;
import com.bit.kodari.dto.RepresentDto;
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
public class RepresentRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    RepresentSql representSql;
    public RepresentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // TODO Represent 대표 코인 등록
    public RepresentDto.PostRepresentRes insert(RepresentDto.PostRepresentReq represent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("portIdx", represent.getPortIdx())
                .addValue("coinIdx", represent.getCoinIdx());
        namedParameterJdbcTemplate.update(representSql.INSERT, parameterSource, keyHolder);
        int pk = keyHolder.getKey().intValue();
        return RepresentDto.PostRepresentRes.builder().representIdx(pk).portIdx(represent.getPortIdx()).coinIdx(represent.getCoinIdx()).build();
    }

    // TODO 대표 코인 조회 - userIdx
    public List<RepresentDto.GetRepresentRes> getRepresent(int portIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        List<RepresentDto.GetRepresentRes> getRepresentRes = namedParameterJdbcTemplate.query(RepresentSql.FIND_USER_REPRESENT, parameterSource,
                (rs, rowNum) -> new RepresentDto.GetRepresentRes(
                        rs.getInt("representIdx"),
                        rs.getInt("portIdx"),
                        rs.getInt("coinIdx"),
                        rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getRepresentRes;
    }

    // TODO 대표 코인 삭제
    public int delete(RepresentDto.DeleteRepresentReq deleteRepresentReq) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("representIdx", deleteRepresentReq.getRepresentIdx());
        return namedParameterJdbcTemplate.update(RepresentSql.DELETE, parameterSource);
    }

    /**
     * 가져오기
     */
    // TODO portIdx로 userIdx 가져오기
    public int getUserIdxByPortIdx(int portIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        return namedParameterJdbcTemplate.query(RepresentSql.GET_USER_IDX_BY_PORT, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    // TODO representIdx로 portIdx 가져오기
    public int getPortIdxByRepresentIdx(int representIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("representIdx", representIdx);
        return namedParameterJdbcTemplate.query(RepresentSql.GET_PORT_IDX_BY_REPRESENT, parameterSource, rs -> {
            int portIdx = 0;
            if (rs.next()) {
                portIdx = rs.getInt("portIdx");
            }

            return portIdx;
        });
    }

    // TODO portIdx로 status 가져오기
    public String getStatusByPortIdx(int portIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("portIdx", portIdx);
        return namedParameterJdbcTemplate.query(RepresentSql.GET_STATUS_BY_PORT, parameterSource, rs -> {
            String status = "";
            if (rs.next()) {
                status = rs.getString("status");
            }

            return status;
        });
    }
}
