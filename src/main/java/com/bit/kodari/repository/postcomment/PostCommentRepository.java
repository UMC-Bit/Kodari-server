package com.bit.kodari.repository.postcomment;

import com.bit.kodari.dto.PostCommentDto;
import com.bit.kodari.dto.PostDto;
import org.springframework.dao.EmptyResultDataAccessException;
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

    //userIdx로 신고 수 가져오기
    public int getUserReport(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        return namedParameterJdbcTemplate.query(PostCommentSql.GET_REPORT_COUNT, parameterSource, rs -> {
            int report_count = 0;
            if (rs.next()) {
                report_count = rs.getInt("report_count");
            }

            return report_count;
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




    //postCommentIdx 댓글 삭제 시 관련된 댓글 좋아요 삭제
    public List<PostCommentDto.GetCommentLikeDeleteRes> getCommentLikeIdxByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        try {
            List<PostCommentDto.GetCommentLikeDeleteRes> getCommentLikeDeleteRes = namedParameterJdbcTemplate.query(PostCommentSql.GET_COMMENT_LIKE_IDX, parameterSource,
                    (rs, rowNum) -> new PostCommentDto.GetCommentLikeDeleteRes(
                            rs.getInt("commentLikeIdx"))
            );
            return getCommentLikeDeleteRes;

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    //postCommentIdx로 댓글 쓴 게시글의 postIdx 가져오기
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




    //댓글 수정
    public int modifyComment(PostCommentDto.PatchCommentReq patchCommentReq) {
        String qry = PostCommentSql.UPDATE_COMMENT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", patchCommentReq.getPostCommentIdx())
                .addValue("userIdx", patchCommentReq.getUserIdx())
                .addValue("postIdx", patchCommentReq.getPostIdx())
                .addValue("content", patchCommentReq.getContent());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //댓글 삭제
    public int modifyCommentStatus(PostCommentDto.PatchDeleteReq patchDeleteReq) {
        String qry = PostCommentSql.DELETE_COMMENT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", patchDeleteReq.getPostCommentIdx());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }


    //삭제된 댓글과 관련된 댓글 좋아요 삭제
    public int deleteCommentLikeStatus(int commentLikeIdx) {
        String qry = PostCommentSql.DELETE_COMMENT_LIKE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("commentLikeIdx", commentLikeIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }



    //토론장 특정 유저의 게시글 조회
    public List<PostCommentDto.GetCommentsRes> getCommentsByUserIdx(int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        try{
            List<PostCommentDto.GetCommentsRes> getCommentsRes = namedParameterJdbcTemplate.query(postCommentSql.LIST_USER_COMMENT, parameterSource,
                    (rs, rowNum) -> new PostCommentDto.GetCommentsRes(
                            rs.getString("content"),
                            rs.getString("time"),
                            getPostByPostIdx(rs.getInt("postIdx"))
                    ));
            return getCommentsRes;
        }catch(EmptyResultDataAccessException e) {
            return null;
        }

    }
    //토론장 특정 댓글의 관련된 게시글 조회
    public List<PostCommentDto.GetPostsRes> getPostByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        List<PostCommentDto.GetPostsRes> getPostsRes = namedParameterJdbcTemplate.query(postCommentSql.LIST_POST, parameterSource,
                    (rs, rowNum) -> new PostCommentDto.GetPostsRes(
                            rs.getInt("postIdx"),
                            rs.getString("symbol"),
                            rs.getString("profileImgUrl"),
                            rs.getString("nickName"),
                            rs.getString("content"),
                            rs.getString("time"),
                            rs.getInt("like"),
                            rs.getInt("dislike"),
                            getCommentCount(rs.getInt("postIdx"))
                    ));
            // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받
            return getPostsRes;

    }

    //postIdx로 댓글 수 가져오기
    public int getCommentCount(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(postCommentSql.GET_COMMENT_COUNT, parameterSource, rs -> {
            int comment_count = 0;
            if (rs.next()) {
                comment_count = rs.getInt("comment_count");
            }

            return comment_count;
        });
    }




//    //토론장 게시글별 댓글 조회
//    public List<PostCommentDto.GetPostCommentRes> getCommentsByPostIdx(int postIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
//        List<PostCommentDto.GetPostCommentRes> getPostCommentRes = namedParameterJdbcTemplate.query(PostCommentSql.LIST_POST_COMMENT, parameterSource,
//                (rs, rowNum) -> new PostCommentDto.GetPostCommentRes(
//                        rs.getString("nickName"),
//                        rs.getString("profileImgUrl"),
//                        rs.getString("content"),
//                        rs.getString("time"),
//                        rs.getInt("like"), false) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//        );
//
//        return getPostCommentRes;
//    }


//    //postIdx로 댓글 쓴 userIdx 가져오기
//    public List<PostCommentDto.GetCommentUserRes> getUserIdxByPostIdx(int postIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
//        try {
//            List<PostCommentDto.GetCommentUserRes> getCommentUserRes = namedParameterJdbcTemplate.query(PostCommentSql.GET_COMMENT_USER_IDX, parameterSource,
//                    (rs, rowNum) -> new PostCommentDto.GetCommentUserRes(
//                            rs.getInt("userIdx"))
//            );
//            return getCommentUserRes;
//
//        } catch (EmptyResultDataAccessException e) {
//            return null;
//        }
//    }







}

