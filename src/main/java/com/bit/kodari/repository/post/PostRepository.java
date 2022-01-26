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
                .addValue("coinIdx", post.getCoinIdx())
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



    //postIdx 게시글 삭제 시 관련된 댓글 삭제
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



    //postIdx 게시글 삭제 시 관련된 댓글 좋아요 삭제
    public List<PostDto.GetCommentLikeDeleteRes> getCommentLikeIdxByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try {
            List<PostDto.GetCommentLikeDeleteRes> getCommentLikeDeleteRes =  namedParameterJdbcTemplate.query(PostSql.GET_COMMENT_LIKE_IDX, parameterSource,
                    (rs, rowNum) -> new PostDto.GetCommentLikeDeleteRes(
                            rs.getInt("commentLikeIdx"))
            );
            return getCommentLikeDeleteRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //postIdx 게시글 삭제 시 관련된 라이크 삭제
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

    //postIdx 게시글 삭제 시 관련된 답글 삭제
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

    //postCommentIdx로 userIdx 가져오기
    public List<PostDto.GetUserIdxRes> getUserIdxByPostCommentIdx(int postCommentIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        try {
            List<PostDto.GetUserIdxRes> getUserIdxRes =  namedParameterJdbcTemplate.query(PostSql.GET_COMMENT_USER_IDX, parameterSource,
                    (rs, rowNum) -> new PostDto.GetUserIdxRes(
                            rs.getInt("userIdx"))
            );
            return getUserIdxRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //postCommentIdx로 userIdx 가져오기
    public List<PostDto.GetUserIdxRes> getUserIdxByPostReplyIdx(int postReplyIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        try {
            List<PostDto.GetUserIdxRes> getUserIdxRes =  namedParameterJdbcTemplate.query(PostSql.GET_REPLY_USER_IDX, parameterSource,
                    (rs, rowNum) -> new PostDto.GetUserIdxRes(
                            rs.getInt("userIdx"))
            );
            return getUserIdxRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //postCommentIdx로 status 가져오기
    public List<PostDto.GetStatusRes> getStatusByPostCommentIdx(int postCommentIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        try {
            List<PostDto.GetStatusRes> getStatusRes =  namedParameterJdbcTemplate.query(PostSql.GET_COMMENT_STATUS, parameterSource,
                    (rs, rowNum) -> new PostDto.GetStatusRes(
                            rs.getString("status"))
            );
            return getStatusRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }


    //postReplyIdx로 status 가져오기
    public List<PostDto.GetStatusRes> getStatusByPostReplyIdx(int postReplyIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        try {
            List<PostDto.GetStatusRes> getStatusRes =  namedParameterJdbcTemplate.query(PostSql.GET_REPLY_STATUS, parameterSource,
                    (rs, rowNum) -> new PostDto.GetStatusRes(
                            rs.getString("status"))
            );
            return getStatusRes;

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

    //삭제된 게시글과 관련된 댓글 삭제
    public int modifyCommentStatus(int postCommentIdx) {
        String qry = PostSql.DELETE_COMMENT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //삭제된 게시글과 관련된 댓글 좋아요 삭제
    public int deleteCommentLikeStatus(int commentLikeIdx) {
        String qry = PostSql.DELETE_COMMENT_LIKE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("commentLikeIdx", commentLikeIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //삭제된 게시글과 관련된 싫어요/좋아요 삭제
    public int deleteLikeStatus(int postLikeIdx) {
        String qry = PostSql.DELETE_LIKE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //삭제된 게시글과 관련된 답글 삭제
    public int modifyReplyStatus(int postReplyIdx) {
        String qry = PostSql.DELETE_REPLY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }




    //토론장 게시글 전체 조회
    public List<PostDto.GetPostRes> getPosts(){
        SqlParameterSource parameterSource = new MapSqlParameterSource();
        List<PostDto.GetPostRes> getPostRes = namedParameterJdbcTemplate.query(PostSql.LIST_POST,parameterSource,
                (rs, rowNum) -> new PostDto.GetPostRes(
                        rs.getString("symbol"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getInt("like"),
                        rs.getInt("dislike")
                // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ));

        return getPostRes;
    }

    //토론장 특정 유저의 게시글 조회
    public List<PostDto.GetPostRes> getPostsByUserIdx(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        List<PostDto.GetPostRes> getPostRes = namedParameterJdbcTemplate.query(PostSql.LIST_USER_POST, parameterSource,
                (rs, rowNum) -> new PostDto.GetPostRes(
                        rs.getString("symbol"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getInt("like"),
                        rs.getInt("dislike")
                        // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ));
        return getPostRes;
    }



    //토론장 특정 게시글의 게시글 조회
    public PostDto.GetUserPostRes getPostsByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try{
        PostDto.GetUserPostRes getUserPostRes = namedParameterJdbcTemplate.queryForObject(PostSql.LIST_POSTS, parameterSource,
                (rs, rowNum) -> {
                        List<PostDto.GetCommentRes> commentList = getCommentByPostIdx(postIdx);

                        PostDto.GetUserPostRes post = new PostDto.GetUserPostRes
                        (rs.getString("symbol"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getInt("like"),
                        rs.getInt("dislike"), false, commentList, false, false,true, true); // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        return post;
    }
    );
    return getUserPostRes;
}catch(EmptyResultDataAccessException e) {
        return null;
        }

        }

    //토론장 특정 게시글의 관련된 댓글 조회
    public List<PostDto.GetCommentRes> getCommentByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try{
        List<PostDto.GetCommentRes> getCommentRes = namedParameterJdbcTemplate.query(PostSql.LIST_COMMENT, parameterSource,
                (rs, rowNum) -> new PostDto.GetCommentRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("nickName"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getInt("like"),
                        getReplyByCommentIdx(rs.getInt("postCommentIdx"))
                ));
        // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받
            return getCommentRes;
    }catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    //토론장 특정 게시글의 관련된 댓글 조회
    public List<PostDto.GetReplyRes> getReplyByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        List<PostDto.GetReplyRes> getReplyRes = namedParameterJdbcTemplate.query(PostSql.LIST_REPLY, parameterSource,
                (rs, rowNum) -> new PostDto.GetReplyRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("nickName"),
                        rs.getString("content"),
                        rs.getString("time")
                        // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                ));
        return getReplyRes;
    }

    //토론장 CommentIdx로 답글 조회
    public List<PostDto.GetReplyRes> getReplyByCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        List<PostDto.GetReplyRes> getReplyRes = namedParameterJdbcTemplate.query(PostSql.LIST_REPLY_BY_COMMENT_ID, parameterSource,
                (rs, rowNum) -> new PostDto.GetReplyRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("nickName"),
                        rs.getString("content"),
                        rs.getString("time")
                        // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                ));
        return getReplyRes;
    }
}
