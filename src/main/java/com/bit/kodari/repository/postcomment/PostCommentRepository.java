package com.bit.kodari.repository.postcomment;

import com.bit.kodari.dto.PostCommentDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostCommentRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    PostCommentSql postCommentSql;
    public PostCommentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 게시글 댓글등록
    public PostCommentDto.RegisterCommentRes insertComment(PostCommentDto.RegisterCommentReq post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", post.getUserIdx())
                .addValue("postIdx", post.getPostIdx())
                .addValue("content", post.getContent());
        int affectedRows = namedParameterJdbcTemplate.update(postCommentSql.INSERT_COMMENT, parameterSource, keyHolder);
        return PostCommentDto.RegisterCommentRes.builder().userIdx(post.getUserIdx()).build();
    }

    //postCommentIdx로 댓글 쓴 userIdx 가져오기
    public int getUserIdxByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(postCommentSql.GET_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    //postCommentIdx로 댓글 쓴 postIdx 가져오기
    public int getPostIdxByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(postCommentSql.GET_POST_IDX, parameterSource, rs -> {
            int postIdx = 0;
            if (rs.next()) {
                postIdx = rs.getInt("postIdx");
            }

            return postIdx;
        });
    }

    //postCommentIdx로 댓글 Status 가져오기
    public String getStatusByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(postCommentSql.GET_STATUS, parameterSource, rs -> {
            String status = " ";
            if (rs.next()) {
                status = rs.getString("status");
            }

            return status;
        });
    }

    //postIdx로 댓글쓴 게시글의 status 가져오기
    public String getStatusByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(postCommentSql.GET_POST_STATUS, parameterSource, rs -> {
            String post_status = " ";
            if (rs.next()) {
                post_status = rs.getString("status");
            }

            return post_status;
        });

    }

    //댓글 수정
    public int modifyComment(PostCommentDto.PatchCommentReq patchCommentReq) {
        String qry = PostCommentSql.UPDATE_COMMENT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", patchCommentReq.getPostCommentIdx())
                .addValue("userIdx", patchCommentReq.getUserIdx())
                .addValue("postIdx", patchCommentReq.getPostIdx())
                .addValue("content", patchCommentReq.getContent());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //게시글 삭제
    public int modifyCommentStatus(PostCommentDto.PatchDeleteReq patchDeleteReq) {
        String qry = PostCommentSql.DELETE_COMMENT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", patchDeleteReq.getPostCommentIdx());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //토론장 게시글별 댓글 조회
    public List<PostCommentDto.GetCommentRes> getCommentsByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        List<PostCommentDto.GetCommentRes> getCommentRes = namedParameterJdbcTemplate.query(PostCommentSql.LIST_POST_COMMENT,parameterSource,
                (rs, rowNum) -> new PostCommentDto.GetCommentRes(
                        rs.getInt("boardIdx"),
                        rs.getString("nickName"),
                        rs.getInt("likeCnt"),
                        rs.getString("content")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getCommentRes;
    }

    //토론장 특정 유저의 게시글 조회
    public List<PostCommentDto.GetCommentRes> getCommentsByUserIdx(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        List<PostCommentDto.GetCommentRes> getCommentsRes = namedParameterJdbcTemplate.query(PostCommentSql.LIST_USER_COMMENT, parameterSource,
                (rs, rowNum) -> new PostCommentDto.GetCommentRes(
                        rs.getInt("boardIdx"),
                        rs.getString("nickName"),
                        rs.getInt("likeCnt"),
                        rs.getString("content"))
        );

        return getCommentsRes;
    }

}
