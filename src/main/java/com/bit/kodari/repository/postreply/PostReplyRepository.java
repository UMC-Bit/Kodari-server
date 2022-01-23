package com.bit.kodari.repository.postreply;

import com.bit.kodari.dto.PostCommentDto;
import com.bit.kodari.dto.PostReplyDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostReplyRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    PostReplySql postReplySql;
    public PostReplyRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 게시글 답글 등록
    public PostReplyDto.RegisterReplyRes insertReply(PostReplyDto.RegisterReplyReq post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", post.getUserIdx())
                .addValue("postCommentIdx", post.getPostCommentIdx())
                .addValue("content", post.getContent());
        int affectedRows = namedParameterJdbcTemplate.update(postReplySql.INSERT_REPLY, parameterSource, keyHolder);
        return PostReplyDto.RegisterReplyRes.builder().userIdx(post.getUserIdx()).build();
    }

    //postReplyIdx로 댓글 쓴 userIdx 가져오기
    public int getUserIdxByPostReplyIdx(int postReplyIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        return namedParameterJdbcTemplate.query(postReplySql.GET_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    //postReplyIdx로 댓글의 postCommentIdx 가져오기
    public int getPostCommentIdxByPostReplyIdx(int postReplyIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        return namedParameterJdbcTemplate.query(postReplySql.GET_COMMENT_IDX, parameterSource, rs -> {
            int postCommentIdx = 0;
            if (rs.next()) {
                postCommentIdx = rs.getInt("postCommentIdx");
            }

            return postCommentIdx;
        });
    }

    //postCommentIdx로 댓글의 status 가져오기
    public String getStatusByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(postReplySql.GET_COMMENT_STATUS, parameterSource, rs -> {
            String comment_status = " ";
            if (rs.next()) {
                comment_status = rs.getString("status");
            }

            return comment_status;
        });

    }

    //postCommentIdx로 게시글의 status 가져오기
    public String getPostStatusByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(postReplySql.GET_POST_STATUS, parameterSource, rs -> {
            String post_status = " ";
            if (rs.next()) {
                post_status = rs.getString("status");
            }

            return post_status;
        });

    }

    //답글 수정
    public int modifyReply(PostReplyDto.PatchReplyReq patchReplyReq) {
        String qry = PostReplySql.UPDATE_REPLY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", patchReplyReq.getPostReplyIdx())
                .addValue("userIdx", patchReplyReq.getUserIdx())
                .addValue("postCommentIdx", patchReplyReq.getPostCommentIdx())
                .addValue("content", patchReplyReq.getContent());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //답글 삭제
    public int modifyReplyStatus(PostReplyDto.PatchReplyDeleteReq patchReplyDeleteReq) {
        String qry = PostReplySql.DELETE_REPLY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", patchReplyDeleteReq.getPostReplyIdx());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //토론장 특정 댓글의 답글 조회
    public List<PostReplyDto.GetReplyRes> getReplyByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        List<PostReplyDto.GetReplyRes> getReplyCommentRes = namedParameterJdbcTemplate.query(PostReplySql.LIST_COMMENT_REPLY, parameterSource,
                (rs, rowNum) -> new PostReplyDto.GetReplyRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("nickName"),
                        rs.getString("content"))
        );

        return getReplyCommentRes;
    }

    //토론장 특정 유저의 답글 조회
    public List<PostReplyDto.GetReplyRes> getReplyByUserIdx(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        List<PostReplyDto.GetReplyRes> getReplyUserRes = namedParameterJdbcTemplate.query(PostReplySql.LIST_USER_REPLY, parameterSource,
                (rs, rowNum) -> new PostReplyDto.GetReplyRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("nickName"),
                        rs.getString("content"))
        );

        return getReplyUserRes;
    }

    //토론장 게시글별 댓글 수 조회
    public List<PostReplyDto.GetReplyCntRes> getReplyCntByPostCommentIdx(int postCommentIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        List<PostReplyDto.GetReplyCntRes> getReplyCntRes = namedParameterJdbcTemplate.query(PostReplySql.LIST_REPLY_CNT,parameterSource,
                (rs, rowNum) -> new PostReplyDto.GetReplyCntRes(
                        rs.getInt("reply_cnt")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );
        return getReplyCntRes;
    }


}
