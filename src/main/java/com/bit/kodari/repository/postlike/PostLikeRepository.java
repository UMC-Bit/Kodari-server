package com.bit.kodari.repository.postlike;

import com.bit.kodari.dto.PostLikeDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostLikeRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    PostLikeSql postLikeSql;
    public PostLikeRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 게시글 좋아요/싫어요 등록
    public PostLikeDto.RegisterLikeRes chooseLike(PostLikeDto.RegisterLikeReq post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userIdx", post.getUserIdx())
                .addValue("postIdx", post.getPostIdx())
                .addValue("likeType", post.getLikeType());
        int affectedRows = namedParameterJdbcTemplate.update(postLikeSql.CHOOSE_LIKE, parameterSource, keyHolder);
        return PostLikeDto.RegisterLikeRes.builder().likeType(post.getLikeType()).build();
    }


    //postLikeIdx로 좋아요/싫어요한 userIdx 가져오기
    public int getUserIdxByPostLikeIdx(int postLikeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
        return namedParameterJdbcTemplate.query(PostLikeSql.GET_LIKE_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
    }

    //postLikeIdx로 좋아요/싫어요한 postIdx 가져오기
    public int getPostIdxByPostLikeIdx(int postLikeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
        return namedParameterJdbcTemplate.query(PostLikeSql.GET_LIKE_POST_IDX, parameterSource, rs -> {
            int postIdx = 0;
            if (rs.next()) {
                postIdx = rs.getInt("postIdx");
            }

            return postIdx;
        });
    }

    //postIdx로 게시글의 Status 가져오기
    public String getStatusByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(PostLikeSql.GET_POST_STATUS, parameterSource, rs -> {
            String post_status = " ";
            if (rs.next()) {
                post_status = rs.getString("status");
            }

            return post_status;
        });
    }

    //postLikeIdx로 Status 가져오기
    public String getStatusByPostLikeIdx(int postLikeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
        return namedParameterJdbcTemplate.query(PostLikeSql.GET_LIKE_STATUS, parameterSource, rs -> {
            String status = " ";
            if (rs.next()) {
                status = rs.getString("status");
            }

            return status;
        });
    }

    //postLikeIdx로 likeType 가져오기
    public int getLikeTypeByPostLikeIdx(int postLikeIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
        return namedParameterJdbcTemplate.query(PostLikeSql.GET_LIKE_TYPE, parameterSource, rs -> {
            int likeType = 1;
            if (rs.next()) {
                likeType = rs.getInt("likeType");
            }

            return likeType;
        });
    }

    //게시글 좋아요/싫어요 수정
    public int modifyLike(PostLikeDto.PatchLikeReq patchLikeReq) {
        String qry = PostLikeSql.UPDATE_POST_LIKE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", patchLikeReq.getPostIdx())
                .addValue("likeType", patchLikeReq.getLikeType());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //게시글 좋아요/싫어요 삭제
    public int deleteLike(PostLikeDto.DeleteLikeReq deleteLikeReq) {
        String qry = PostLikeSql.DELETE_POST_LIKE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", deleteLikeReq.getPostIdx());
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //토론장 게시글별 좋아요 조회
    public List<PostLikeDto.GetLikeRes> getLikesByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        List<PostLikeDto.GetLikeRes> getLikeRes = namedParameterJdbcTemplate.query(PostLikeSql.LIST_POST_LIKE,parameterSource,
                (rs, rowNum) -> new PostLikeDto.GetLikeRes(
                        rs.getInt("likeType")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getLikeRes;
    }

    //토론장 게시글별 싫어요 조회
    public List<PostLikeDto.GetDislikeRes> getDislikesByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        List<PostLikeDto.GetDislikeRes> getDislikeRes = namedParameterJdbcTemplate.query(PostLikeSql.LIST_POST_DISLIKE,parameterSource,
                (rs, rowNum) -> new PostLikeDto.GetDislikeRes(
                        rs.getInt("likeType")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getDislikeRes;
    }





}
