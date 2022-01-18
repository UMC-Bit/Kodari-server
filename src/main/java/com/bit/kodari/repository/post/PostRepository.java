package com.bit.kodari.repository.post;

import com.bit.kodari.dto.PostDto;
import com.bit.kodari.repository.post.PostSql;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    PostSql PostSql;
    public PostRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 게시글 등록
    public PostDto.RegisterRes insert(PostDto.RegisterReq post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("boardIdx", post.getBoardIdx())
                .addValue("userIdx", post.getUserIdx())
                .addValue("content", post.getContent());
        int affectedRows = namedParameterJdbcTemplate.update(PostSql.INSERT_POST, parameterSource, keyHolder);
        return PostDto.RegisterRes.builder().userIdx(post.getUserIdx()).build();
    }

    //postIdx로 userIdx 가져오기
    public int getUserIdxByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(PostSql.GET_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    //postIdx로 Status 가져오기
    public String getStatusByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(PostSql.GET_STATUS, parameterSource, rs -> {
            String status = " ";
            if (rs.next()) {
                status = rs.getString("status");
            }

            return status;
        });
    }

    //게시글 수정
    public int modifyPost(PostDto.PatchPostReq patchPostReq) {
        String qry = PostSql.UPDATE_POST;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", patchPostReq.getPostIdx())
                .addValue("content", patchPostReq.getContent());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //게시글 삭제
    public int modifyPostStatus(PostDto.PatchDeleteReq patchDeleteReq) {
        String qry = PostSql.DELETE_POST;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", patchDeleteReq.getPostIdx());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //postIdx로 postCommentIdx 받아오기
    public List<PostDto.CommentDeleteReq> getCommentIdxByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx)
        try {
            List<PostDto.CommentDeleteReq> getAccountNameRes =  namedParameterJdbcTemplate.query(AccountSql.GET_ACCOUNT_NAME, parameterSource,
                    (rs, rowNum) -> new AccountDto.GetAccountNameRes(
                            rs.getString("accountName"))
            );
            return getAccountNameRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }

    }

    //토론장 게시글 조회
    public List<PostDto.GetPostRes> getPosts(){
        SqlParameterSource parameterSource = new MapSqlParameterSource();
        List<PostDto.GetPostRes> getPostRes = namedParameterJdbcTemplate.query(PostSql.LIST_POST,parameterSource,
                (rs, rowNum) -> new PostDto.GetPostRes(
                        rs.getString("boardName"),
                        rs.getString("nickName"),
                        rs.getString("content")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getPostRes;
    }

    //토론장 특정 유저의 게시글 조회
    public List<PostDto.GetPostRes> getPostsByUserIdx(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        List<PostDto.GetPostRes> getPostRes = namedParameterJdbcTemplate.query(PostSql.LIST_USER_POST, parameterSource,
                (rs, rowNum) -> new PostDto.GetPostRes(
                        rs.getString("boardName"),
                        rs.getString("nickName"),
                        rs.getString("content")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getPostRes;
    }




}
