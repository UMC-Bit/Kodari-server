package com.bit.kodari.repository.user;

import com.bit.kodari.dto.UserDto;
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
public class UserRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    UserSql userSql;
    public UserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    // 회원가입: User 레코드 추가
    public UserDto.PostUserRes createUser(UserDto.PostUserReq postUserReq) {
        // User 레코드 추가
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource("nickName", postUserReq.getNickName())
                .addValue("email", postUserReq.getEmail())
                .addValue("password", postUserReq.getPassword())
                .addValue("profileImgUrl", postUserReq.getProfileImgUrl())
                .addValue("authKey", postUserReq.getAuthKey())
                .addValue("status", "active");
        int affectedRows = namedParameterJdbcTemplate.update(UserSql.INSERT, parameterSource, keyHolder);

        // 추가된 정보를 postUserRes 형태로 반환
        //String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        SqlParameterSource parameterSource2 = new MapSqlParameterSource("nickName",postUserReq.getNickName());
        UserDto.PostUserRes postUserRes = namedParameterJdbcTemplate.queryForObject(UserSql.FIND_BY_NICKNAME,parameterSource2,
                (rs, rowNum) -> new UserDto.PostUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("password")
                ));
        return postUserRes;
        //return new UserDto.PostUserRes(postUserReq.getNickName());
    }

    // 로그인: 해당 email에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public UserDto.User getPwd(UserDto.PostLoginReq postLoginReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("email",postLoginReq.getEmail());
        try {
            return namedParameterJdbcTemplate.queryForObject(UserSql.FIND_BY_EMAIL,parameterSource,
                    (rs, rowNum) -> new UserDto.User(
                            rs.getInt("userIdx"),
                            rs.getString("nickName"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profileImgUrl"),
                            rs.getString("authKey"),
                            rs.getString("status") // RowMapper(): 원하는 결과값 형태로 받기
                    ));
            //return postLoginRes;

        } catch (EmptyResultDataAccessException e){
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }

    }



    // User 조회 : 전체 유저 UserDto.User 전달
    public List<UserDto.GetUserRes> getUsers(){
        List<UserDto.GetUserRes> getUsersRes = namedParameterJdbcTemplate.query(UserSql.FIND_USERS,
                // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                (rs, rowNum) -> new UserDto.GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("profileImgUrl"),
                        rs.getString("status")) // RowMapper(): 원하는 결과값 형태로 받기
        );
        return getUsersRes;
    }

    // User 조회: 해당 nickName을 갖는 유저 조회
    public List<UserDto.GetUserRes> getUserByNickname(String nickName){
        SqlParameterSource parameterSource = new MapSqlParameterSource("nickName", nickName); // 닉네임 값 전달 객체
        try{
            // nickName을 갖는 유저 조회
            List<UserDto.GetUserRes> getUserRes = namedParameterJdbcTemplate.query(UserSql.FIND_BY_NICKNAME, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new UserDto.GetUserRes(
                            rs.getInt("userIdx"),
                            rs.getString("nickName"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profileImgUrl"),
                            rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getUserRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }
    }

    // User 조회: 해당 email 갖는 유저 조회
    public List<UserDto.GetUserRes> getUserByEmail(String email){
        SqlParameterSource parameterSource = new MapSqlParameterSource("email",email); // email 값 전달 객체
        try{
            List<UserDto.GetUserRes> getUserRes = namedParameterJdbcTemplate.query(UserSql.FIND_BY_EMAIL, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new UserDto.GetUserRes(
                            rs.getInt("userIdx"),
                            rs.getString("nickName"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profileImgUrl"),
                            rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getUserRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }

    }


    // User 조회: 해당 userIdx 갖는 유저 조회
    public List<UserDto.GetUserRes> getUserByUserIdx(String userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx",userIdx); // userIdx 값 전달 객체
        try{
            List<UserDto.GetUserRes> getUserRes = namedParameterJdbcTemplate.query(UserSql.FIND_BY_USERIDX, parameterSource,
                    // 이 자리에 new getUserMapper() 생성해서 넣어주거나 람다식으로 바로 생성해서 넘겨주기
                    (rs, rowNum) -> new UserDto.GetUserRes(
                            rs.getInt("userIdx"),
                            rs.getString("nickName"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profileImgUrl"),
                            rs.getString("status")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
            );
            return getUserRes;
        }
        catch (EmptyResultDataAccessException e) {
            // EmptyResultDataAccessException 예외 발생시 null 리턴
            return null;
        }

    }


    // 유저인덱스로 status 조회,
    public String getStatusByUserIdx(int userIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx",userIdx);
        return namedParameterJdbcTemplate.queryForObject(UserSql.FIND_STATUS_BY_USERIDX,parameterSource,String.class);
    }



    // 회원 삭제
    public int deleteUser(UserDto.DeleteUserReq deleteUserReq){
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", deleteUserReq.getUserIdx());
        return namedParameterJdbcTemplate.update(UserSql.DELETE, parameterSource); // 변경 셩공하면 1 반환, 실패시 0 반환
    }

    // 회원 닉네임 변경
    public int updateNickName(UserDto.UpdateNickNameReq updateNickNameReq){
        //SqlParameterSource parameterSource = new MapSqlParameterSource("nickName", updateNickNameReq.getNickName());
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", updateNickNameReq.getUserIdx())
                .addValue("nickName", updateNickNameReq.getNickName());
        return namedParameterJdbcTemplate.update(UserSql.UPDATE_NICKNAME, parameterSource);
    }

    // 회원 프로필사진 변경
    public int updateProfileImgUrl(UserDto.UpdateProfileImgUrlReq updateProfileImgUrlReq){
        //SqlParameterSource parameterSource = new MapSqlParameterSource("nickName", updateNickNameReq.getNickName());
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", updateProfileImgUrlReq.getUserIdx())
                .addValue("profileImgUrl", updateProfileImgUrlReq.getProfileImgUrl());

        return namedParameterJdbcTemplate.update(UserSql.UPDATE_PROFILEIMGURL, parameterSource);
    }

    // 회원 패스워드 변경
    public int updatePassword(UserDto.UpdatePasswordReq updatePasswordReq){
        //SqlParameterSource parameterSource = new MapSqlParameterSource("nickName", updateNickNameReq.getNickName());
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", updatePasswordReq.getUserIdx())
                .addValue("password", updatePasswordReq.getPassword());

        return namedParameterJdbcTemplate.update(UserSql.UPDATE_PASSWORD, parameterSource);
    }


}
