package com.bit.kodari.repository.postlike;

import com.bit.kodari.dto.PostDto;
import com.bit.kodari.dto.PostLikeDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostLikeRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    PostLikeSql postLikeSql;
    public PostLikeRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 게시글 좋아요/싫어요 등록
    public PostLikeDto.PostLikeRes chooseLike(PostLikeDto.RegisterLikeReq like) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", like.getUserIdx())
                .addValue("postIdx", like.getPostIdx())
                .addValue("likeType", like.getLikeType());
        int affectedRows = namedParameterJdbcTemplate.update(postLikeSql.CHOOSE_LIKE, parameterSource, keyHolder);
        return new PostLikeDto.PostLikeRes(like.getUserIdx(), keyHolder.getKey().intValue());
    }



    //게시글 좋아요/싫어요 삭제
    public PostLikeDto.PostLikeRes deleteLike(PostLikeDto.PostLikeReq deleteLikeReq) {
        String qry = PostLikeSql.DELETE_POST_LIKE;
        int postLikeId = deleteLikeReq.getPostLikeIdx();
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", deleteLikeReq.getPostLikeIdx());
        namedParameterJdbcTemplate.update(qry, parameterSource);
        PostLikeDto.PostLikeRes deleteLikeRes = new PostLikeDto.PostLikeRes(deleteLikeReq.getUserIdx(), postLikeId);
        return deleteLikeRes;
    }

    //postIdx로 게시글의 Status 가져오기
    public String getStatusByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(postLikeSql.GET_POST_STATUS, parameterSource, rs -> {
            String post_status = " ";
            if (rs.next()) {
                post_status = rs.getString("status");
            }

            return post_status;
        });
    }

    //postLikeIdx로 게시글의 userIdx 가져오기
    public int getUserIdxByPostLikeIdx(int postLikeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
        return namedParameterJdbcTemplate.query(postLikeSql.GET_USERIDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }


    //userIdx와 postIdx로 postLikeIdx가져오기
    public int getPostLikeIdxByIdx(int userIdx, int postIdx, int likeType) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("postIdx", postIdx)
                .addValue("likeType", likeType);
        return namedParameterJdbcTemplate.query(postLikeSql.GET_POST_LIKE_IDX, parameterSource, rs -> {
            int postLikeIdx = 0;
            if (rs.next()) {
                postLikeIdx = rs.getInt("postLikeIdx");
            }

            return postLikeIdx;
        });
    }


    //userIdx, postIdx로 유저가 또 있는 확인하기
    public String getUser(int userIdx, int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(postLikeSql.EXIST_USER, parameterSource, rs -> {
            String exist = " ";
            if (rs.next()) {
                exist = rs.getString("exist");
            }

            return exist;
        });
    }

    //같은 like 타입을 골랐는지 확인
    public int getLikeType(int userIdx, int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
                .addValue("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(postLikeSql.EQUAL_LIKE, parameterSource, rs -> {
            int likeType = 1;
            if (rs.next()) {
                likeType = rs.getInt("likeType");
            }

            return likeType;
        });
    }

//
//    //postLikeIdx로 좋아요/싫어요한 userIdx 가져오기
//    public int getUserIdxByPostLikeIdx(int postLikeIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
//        return namedParameterJdbcTemplate.query(postLikeSql.GET_LIKE_USER_IDX, parameterSource, rs -> {
//            int userIdx = 0;
//            if (rs.next()) {
//                userIdx = rs.getInt("userIdx");
//            }
//
//            return userIdx;
//        });
//    }
//
//    //postLikeIdx로 좋아요/싫어요한 postIdx 가져오기
//    public int getPostIdxByPostLikeIdx(int postLikeIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
//        return namedParameterJdbcTemplate.query(postLikeSql.GET_LIKE_POST_IDX, parameterSource, rs -> {
//            int postIdx = 0;
//            if (rs.next()) {
//                postIdx = rs.getInt("postIdx");
//            }
//
//            return postIdx;
//        });
//    }





//    //postLikeIdx로 Status 가져오기
//    public String getStatusByPostLikeIdx(int postLikeIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
//        return namedParameterJdbcTemplate.query(postLikeSql.GET_LIKE_STATUS, parameterSource, rs -> {
//            String status = " ";
//            if (rs.next()) {
//                status = rs.getString("status");
//            }
//
//            return status;
//        });
//    }
//
//    //postLikeIdx로 likeType 가져오기
//    public int getLikeTypeByPostLikeIdx(int postLikeIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
//        return namedParameterJdbcTemplate.query(postLikeSql.GET_LIKE_TYPE, parameterSource, rs -> {
//            int likeType = 1;
//            if (rs.next()) {
//                likeType = rs.getInt("likeType");
//            }
//
//            return likeType;
//        });
//    }

//
//    //userIdx, postIdx로 likeType 가져오기
//    public int getLikeTypeByIdx(int userIdx, int postIdx) {
//        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx)
//                .addValue("postIdx", postIdx);
//        return namedParameterJdbcTemplate.query(postLikeSql.GET_LIKE_TYPE, parameterSource, rs -> {
//            int likeType = 1;
//            if (rs.next()) {
//                likeType = rs.getInt("likeType");
//            }
//
//            return likeType;
//        });
//    }
//
//    //게시글 좋아요/싫어요 수정
//    public int modifyLike(PostLikeDto.PatchLikeReq patchLikeReq) {
//        String qry = PostLikeSql.UPDATE_POST_LIKE;
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", patchLikeReq.getPostLikeIdx())
//                .addValue("likeType", patchLikeReq.getLikeType());
//        return namedParameterJdbcTemplate.update(qry, parameterSource);
//    }




//    //토론장 게시글별 좋아요 조회
//    public List<PostLikeDto.GetLikeRes> getLikesByPostIdx(int postIdx){
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
//        List<PostLikeDto.GetLikeRes> getLikeRes = namedParameterJdbcTemplate.query(postLikeSql.LIST_POST_LIKE,parameterSource,
//                (rs, rowNum) -> new PostLikeDto.GetLikeRes(
//                        rs.getInt("true_cnt")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//        );
//        return getLikeRes;
//    }
//
//    //토론장 게시글별 싫어요 조회
//    public List<PostLikeDto.GetDislikeRes> getDislikesByPostIdx(int postIdx){
//        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
//        List<PostLikeDto.GetDislikeRes> getDislikeRes = namedParameterJdbcTemplate.query(postLikeSql.LIST_POST_DISLIKE,parameterSource,
//                (rs, rowNum) -> new PostLikeDto.GetDislikeRes(
//                        rs.getInt("dislikeCnt")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//        );
//
//        return getDislikeRes;
//    }





}
