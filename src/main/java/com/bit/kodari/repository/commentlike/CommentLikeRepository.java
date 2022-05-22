package com.bit.kodari.repository.commentlike;

import com.bit.kodari.dto.CommentLikeDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CommentLikeRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    CommentLikeSql commentLikeSql;
    public CommentLikeRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 댓글 좋아요 등록
    public CommentLikeDto.CommentLikeRes chooseCommentLike(CommentLikeDto.RegisterCommentLikeReq like) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", like.getUserIdx())
                .addValue("postCommentIdx", like.getPostCommentIdx());
        int affectedRows = namedParameterJdbcTemplate.update(CommentLikeSql.CHOOSE_COMMENT_LIKE, parameterSource, keyHolder);
        return new CommentLikeDto.CommentLikeRes(like.getUserIdx(), keyHolder.getKey().intValue());
    }


    //userIdx와 postCommentIdx로 commentLikeIdx가져오기
    public int getCommentLikeIdxByIdx(int userIdx, int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(commentLikeSql.GET_COMMENT_LIKE_IDX, parameterSource, rs -> {
            int commentLikeIdx = 0;
            if (rs.next()) {
                commentLikeIdx = rs.getInt("commentLikeIdx");
            }

            return commentLikeIdx;
        });
    }

    //같은 postCommentIdx에 유저가 또 있는 확인하기
    public String getUser(int userIdx, int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(commentLikeSql.EXIST_USER, parameterSource, rs -> {
            String exist = " ";
            if (rs.next()) {
                exist = rs.getString("exist");
            }

            return exist;
        });
    }

    //postCommentIdx로 댓글의 Status 가져오기
    public String getStatusByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(commentLikeSql.GET_COMMENT_STATUS, parameterSource, rs -> {
            String comment_status = " ";
            if (rs.next()) {
                comment_status = rs.getString("status");
            }

            return comment_status;
        });
    }

    //댓글 좋아요 삭제
    public CommentLikeDto.CommentLikeRes deleteLike(CommentLikeDto.CommentLikeReq deleteLikeReq) {
        String qry = CommentLikeSql.DELETE_COMMENT_LIKE;
        int commentLikeId = deleteLikeReq.getCommentLikeIdx();
        SqlParameterSource parameterSource = new MapSqlParameterSource("commentLikeIdx", deleteLikeReq.getCommentLikeIdx());
        namedParameterJdbcTemplate.update(qry, parameterSource);
        CommentLikeDto.CommentLikeRes deleteLikeRes = new CommentLikeDto.CommentLikeRes(deleteLikeReq.getUserIdx(), commentLikeId);
        return deleteLikeRes;
    }

//    //postCommentIdx로 게시글의 Status 가져오기
//    public String getPostStatusByPostCommentIdx(int postCommentIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
//        return namedParameterJdbcTemplate.query(commentLikeSql.GET_POST_STATUS, parameterSource, rs -> {
//            String post_status = " ";
//            if (rs.next()) {
//                post_status = rs.getString("status");
//            }
//
//            return post_status;
//        });
//    }
//
//    //commentLikeIdx로 like 가져오기
//    public int getLikeByCommentLikeIdx(int commentLikeIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("commentLikeIdx", commentLikeIdx);
//        return namedParameterJdbcTemplate.query(commentLikeSql.GET_LIKE, parameterSource, rs -> {
//            int like = 0;
//            if (rs.next()) {
//                like = rs.getInt("like");
//            }
//
//            return like;
//        });
//    }

    //    //commentLikeIdx로 좋아요한 userIdx 가져오기
//    public int getUserIdxByCommentLikeIdx(int commentLikeIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("commentLikeIdx", commentLikeIdx);
//        return namedParameterJdbcTemplate.query(commentLikeSql.GET_LIKE_USER_IDX, parameterSource, rs -> {
//            int userIdx = 0;
//            if (rs.next()) {
//                userIdx = rs.getInt("userIdx");
//            }
//
//            return userIdx;
//        });
//    }
//
//    //commentLikeIdx로 좋아요 postCommentIdx 가져오기
//    public int getPostCommentIdxByCommentLikeIdx(int commentLikeIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("commentLikeIdx", commentLikeIdx);
//        return namedParameterJdbcTemplate.query(commentLikeSql.GET_LIKE_POST_IDX, parameterSource, rs -> {
//            int postCommentIdx = 0;
//            if (rs.next()) {
//                postCommentIdx = rs.getInt("postCommentIdx");
//            }
//
//            return postCommentIdx;
//        });
//    }


//    //댓글 좋아요 수정
//    public int modifyLike(CommentLikeDto.PatchLikeReq patchLikeReq) {
//        String qry = CommentLikeSql.UPDATE_COMMENT_LIKE;
//        SqlParameterSource parameterSource = new MapSqlParameterSource("commentLikeIdx", patchLikeReq.getCommentLikeIdx())
//                .addValue("like", patchLikeReq.getLike());
//        return namedParameterJdbcTemplate.update(qry, parameterSource);
//    }




}
