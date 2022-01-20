package com.bit.kodari.repository.post;

import com.bit.kodari.dto.PostDto;
import com.bit.kodari.repository.post.PostSql;
import org.springframework.dao.EmptyResultDataAccessException;
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

    //postIdx 게시글 삭제 시 댓글 삭제
    public List<PostDto.GetCommentDeleteRes> getPostCommentIdxByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try {
            List<PostDto.GetCommentDeleteRes> getCommentDeleteRes =  namedParameterJdbcTemplate.query(PostSql.GET_COMMENT_IDX, parameterSource,
                    (rs, rowNum) -> new PostDto.GetCommentDeleteRes(
                            rs.getInt("postCommentIdx"))
            );
            return getCommentDeleteRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //postIdx 게시글 삭제 시 라이크 삭제
    public List<PostDto.GetLikeDeleteRes> getPostLikeIdxByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try {
            List<PostDto.GetLikeDeleteRes> getLikeDeleteRes =  namedParameterJdbcTemplate.query(PostSql.GET_LIKE_IDX, parameterSource,
                    (rs, rowNum) -> new PostDto.GetLikeDeleteRes(
                            rs.getInt("postLikeIdx"))
            );
            return getLikeDeleteRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //postIdx 게시글 삭제 시 답글 삭제
    public List<PostDto.GetReplyDeleteRes> getReplyIdxByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try {
            List<PostDto.GetReplyDeleteRes> getReplyDeleteRes =  namedParameterJdbcTemplate.query(PostSql.GET_REPLY_IDX, parameterSource,
                    (rs, rowNum) -> new PostDto.GetReplyDeleteRes(
                            rs.getInt("postReplyIdx"))
            );
            return getReplyDeleteRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
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

    //게시글 댓글 삭제
    public int modifyCommentStatus(int postCommentIdx) {
        String qry = PostSql.DELETE_COMMENT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }
    //게시글 싫어요/좋아요 삭제
    public int deleteLikeStatus(int postLikeIdx) {
        String qry = PostSql.DELETE_LIKE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //게시글 답글 삭제
    public int modifyReplyStatus(int postReplyIdx) {
        String qry = PostSql.DELETE_REPLY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
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
