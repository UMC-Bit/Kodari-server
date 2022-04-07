package com.bit.kodari.repository.firebase;

import com.bit.kodari.repository.firebase.FcmSql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class FcmRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    FcmSql fcmSql;
    public FcmRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // userIdx로 token 가져오기
    public String getTokenByUserIdx(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        return namedParameterJdbcTemplate.query(FcmSql.GET_USER_TOKEN, parameterSource, rs -> {
            String token = "";
            if (rs.next()) {
                token = rs.getString("token");
            }

            return token;
        });
    }
}
