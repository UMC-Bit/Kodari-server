package com.bit.kodari.repository.email;

import com.bit.kodari.dto.EmailDto;
import com.bit.kodari.repository.user.UserSql;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmailRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    EmailSql emailSql;
    public EmailRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // 해당 유저에게 이메일 인증 안내 보내기
    public EmailDto.UserEmail getToUser(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        EmailDto.UserEmail getToUser = namedParameterJdbcTemplate.queryForObject(EmailSql.GET_USER_INFO,parameterSource,
                // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                (rs, rowNum) -> new EmailDto.UserEmail(
                        userIdx,
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("authKey")) // RowMapper(): 원하는 결과값 형태로 받기
        );
        return getToUser;
    }

    // 이메일 인증하는 유저가 맞는지 확인하기
    public String checkUser(String email, String authKey){
        SqlParameterSource parameterSource = new MapSqlParameterSource("email", email)
                .addValue("authKey", authKey);
        return namedParameterJdbcTemplate.query(emailSql.CHECK_AUTHKEY, parameterSource, rs -> {
            String check = " ";
            if (rs.next()) {
                check = rs.getString("check");
            }

            return check;
        });
    }

    // 이메일 인증된 유저의 authkey 수정
    public int updateUser (EmailDto.UpdateAuthKey update){
        SqlParameterSource parameterSource = new MapSqlParameterSource("authKey", update.getAuthKey());
        return namedParameterJdbcTemplate.update(EmailSql.UPDATE_AUTHKEY, parameterSource);
    }

    //유저 이메일로 authKey 가져오기
    public String getAuthKeyByEmail(String email) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("email", email);
        return namedParameterJdbcTemplate.query(emailSql.GET_AUTHKEY, parameterSource, rs -> {
            String check_authKey = " ";
            if (rs.next()) {
                check_authKey = rs.getString("authKey");
            }

            return check_authKey;
        });
    }

}
