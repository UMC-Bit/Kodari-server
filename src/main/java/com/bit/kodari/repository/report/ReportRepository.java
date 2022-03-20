package com.bit.kodari.repository.report;

import com.bit.kodari.dto.CommentLikeDto;
import com.bit.kodari.dto.PostDto;
import com.bit.kodari.dto.ReportDto;
import com.bit.kodari.repository.commentlike.CommentLikeSql;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    ReportSql reportSql;
    public ReportRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 게시글 신고
    public ReportDto.PostReportRes choosePostReport(ReportDto.RegisterPostReportReq report, int respondent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("postIdx", report.getPostIdx()) //신고할 게시글 인덱스
                .addValue("reporter", report.getReporter()) //신고하는 유저
                .addValue("respondent", respondent) //신고 당하는 유저
                .addValue("reason", report.getReason()); //신고 사유
        int affectedRows = namedParameterJdbcTemplate.update(reportSql.REPORT_POST, parameterSource, keyHolder);
//    return new ReportDto.PostReportRes(report.getReporter(), keyHolder.getKey().intValue());
        return new ReportDto.PostReportRes(report.getReporter());
    }




    //postReport 테이블에 해당 게시글의 신고횟수 세기
    public int getPostReportCnt(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_REPORT_CNT, parameterSource, rs -> {
            int cnt = 0;
            if (rs.next()) {
                cnt = rs.getInt("postReportCnt");
            }

            return cnt;
        });
    }

    //postIdx로 신고당한유저(respondent) 가져오기 = 게시글 쓴 유저
    public int getRespondentByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_USER_IDX, parameterSource, rs -> {
            int respondent = 0;
            if (rs.next()) {
                respondent = rs.getInt("userIdx");
            }

            return respondent;
        });
    }

    //userIdx, postIdx로 게시글 신고 유저 확인
    public boolean getPostExistUser(int postIdx, int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx",postIdx)
                .addValue("userIdx", userIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_EXIST_USER, parameterSource, rs -> {
            boolean post_exist = false;
            if (rs.next()) {
                post_exist = rs.getBoolean("user");
            }

            return post_exist;
        });
    }

    //신고 3회 초과된 게시글 삭제
    public ReportDto.PostReportRes deletePost(ReportDto.DeletePost deletePost) {
        String qry = ReportSql.DELETE_POST;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", deletePost.getPostIdx());
        namedParameterJdbcTemplate.update(qry, parameterSource);
        ReportDto.PostReportRes deleteRes = new ReportDto.PostReportRes(getRespondentByPostIdx(deletePost.getPostIdx()));
        return deleteRes;
    }

    //토론장 게시글 삭제 시 유저 report + 1
    public ReportDto.UserReportRes updateUserReport(int userIdx) {
        String qry = reportSql.ADD_USER_REPORT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("userIdx", userIdx);
        namedParameterJdbcTemplate.update(qry, parameterSource);
        ReportDto.UserReportRes userReportRes = new ReportDto.UserReportRes(userIdx);
        return userReportRes;
    }




    //postIdx 게시글 삭제 시 관련된 댓글 삭제
    public List<ReportDto.GetCommentDeleteRes> getPostCommentIdxByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try {
            List<ReportDto.GetCommentDeleteRes> getCommentDeleteRes =  namedParameterJdbcTemplate.query(reportSql.GET_COMMENT_IDX, parameterSource,
                    (rs, rowNum) -> new ReportDto.GetCommentDeleteRes(
                            rs.getInt("postCommentIdx"))
            );
            return getCommentDeleteRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }



    //postIdx 게시글 삭제 시 관련된 댓글 좋아요 삭제
    public List<ReportDto.GetCommentLikeDeleteRes> getCommentLikeIdxByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try {
            List<ReportDto.GetCommentLikeDeleteRes> getCommentLikeDeleteRes =  namedParameterJdbcTemplate.query(reportSql.GET_COMMENT_LIKE_IDX, parameterSource,
                    (rs, rowNum) -> new ReportDto.GetCommentLikeDeleteRes(
                            rs.getInt("commentLikeIdx"))
            );
            return getCommentLikeDeleteRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //postIdx 게시글 삭제 시 관련된 라이크 삭제
    public List<ReportDto.GetLikeDeleteRes> getPostLikeIdxByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try {
            List<ReportDto.GetLikeDeleteRes> getLikeDeleteRes =  namedParameterJdbcTemplate.query(reportSql.GET_LIKE_IDX, parameterSource,
                    (rs, rowNum) -> new ReportDto.GetLikeDeleteRes(
                            rs.getInt("postLikeIdx"))
            );
            return getLikeDeleteRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    //postIdx 게시글 삭제 시 관련된 답글 삭제
    public List<ReportDto.GetReplyDeleteRes> getReplyIdxByPostIdx(int postIdx){
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        try {
            List<ReportDto.GetReplyDeleteRes> getReplyDeleteRes =  namedParameterJdbcTemplate.query(reportSql.GET_REPLY_IDX, parameterSource,
                    (rs, rowNum) -> new ReportDto.GetReplyDeleteRes(
                            rs.getInt("postReplyIdx"))
            );
            return getReplyDeleteRes;

        }catch(EmptyResultDataAccessException e){
            return null;
        }
    }


    //삭제된 게시글과 관련된 댓글 삭제
    public int modifyCommentStatus(int postCommentIdx) {
        String qry = reportSql.POST_DELETE_COMMENT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //삭제된 게시글과 관련된 댓글 좋아요 삭제
    public int deleteCommentLikeStatus(int commentLikeIdx) {
        String qry = reportSql.POST_DELETE_COMMENT_LIKE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("commentLikeIdx", commentLikeIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //삭제된 게시글과 관련된 싫어요/좋아요 삭제
    public int deleteLikeStatus(int postLikeIdx) {
        String qry = reportSql.POST_DELETE_LIKE;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postLikeIdx", postLikeIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }

    //삭제된 게시글과 관련된 답글 삭제
    public int modifyReplyStatus(int postReplyIdx) {
        String qry = reportSql.POST_DELETE_REPLY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        return namedParameterJdbcTemplate.update(qry, parameterSource);
    }



    //
    //
    //
    //토론장 댓글 신고
    //
    //
    //



    //토론장 댓글 신고
    public ReportDto.PostCommentReportRes choosePostCommentReport(ReportDto.RegisterPostCommentReportReq report, int respondent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("postCommentIdx", report.getPostCommentIdx()) //신고할 댓글 인덱스
                .addValue("reporter", report.getReporter()) //신고하는 유저
                .addValue("respondent", respondent) //신고 당하는 유저
                .addValue("reason", report.getReason()); //신고 사유
        int affectedRows = namedParameterJdbcTemplate.update(reportSql.REPORT_POST_COMMENT, parameterSource, keyHolder);
//    return new ReportDto.PostReportRes(report.getReporter(), keyHolder.getKey().intValue());
        return new ReportDto.PostCommentReportRes(report.getReporter());
    }



    //postCommentReport 테이블에 해당 댓글의 신고횟수 세기
    public int getPostCommentReportCnt(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_COMMENT_REPORT_CNT, parameterSource, rs -> {
            int cnt = 0;
            if (rs.next()) {
                cnt = rs.getInt("postCommentReportCnt");
            }

            return cnt;
        });
    }

    //postCommentIdx로 신고당한유저(respondent) 가져오기 = 댓글 쓴 유저
    public int getRespondentByPostCommentIdx(int postCommentIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", postCommentIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_COMMENT_USER_IDX, parameterSource, rs -> {
            int respondent = 0;
            if (rs.next()) {
                respondent = rs.getInt("userIdx");
            }

            return respondent;
        });
    }

    //userIdx, postCommentIdx로 댓글 신고 유저 확인
    public boolean getPostCommentExistUser(int postCommentIdx, int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx",postCommentIdx)
                .addValue("userIdx", userIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_COMMENT_EXIST_USER, parameterSource, rs -> {
            boolean comment_exist = false;
            if (rs.next()) {
                comment_exist = rs.getBoolean("user");
            }

            return comment_exist;
        });
    }

    //신고 3회 초과된 댓글 삭제
    public ReportDto.PostCommentReportRes deletePostComment(ReportDto.DeletePostComment deletePostComment) {
        String qry = ReportSql.DELETE_POST_COMMENT;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postCommentIdx", deletePostComment.getPostCommentIdx());
        namedParameterJdbcTemplate.update(qry, parameterSource);
        ReportDto.PostCommentReportRes deleteRes = new ReportDto.PostCommentReportRes(getRespondentByPostCommentIdx(deletePostComment.getPostCommentIdx()));
        return deleteRes;
    }




    //토론장 답글 신고




    //토론장 답글 신고
    public ReportDto.PostReplyReportRes choosePostReplyReport(ReportDto.RegisterPostReplyReportReq report, int respondent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("postReplyIdx", report.getPostReplyIdx()) //신고할 답글 인덱스
                .addValue("reporter", report.getReporter()) //신고하는 유저
                .addValue("respondent", respondent) //신고 당하는 유저
                .addValue("reason", report.getReason()); //신고 사유
        int affectedRows = namedParameterJdbcTemplate.update(reportSql.REPORT_POST_REPLY, parameterSource, keyHolder);
//    return new ReportDto.PostReportRes(report.getReporter(), keyHolder.getKey().intValue());
        return new ReportDto.PostReplyReportRes(report.getReporter());
    }




    //postReplyReport 테이블에 해당 답글의 신고횟수 세기
    public int getPostReplyReportCnt(int postReplyIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_REPLY_REPORT_CNT, parameterSource, rs -> {
            int cnt = 0;
            if (rs.next()) {
                cnt = rs.getInt("postReplyReportCnt");
            }

            return cnt;
        });
    }

    //postReplyIdx로 신고당한유저(respondent) 가져오기 = 답글 쓴 유저
    public int getRespondentByPostReplyIdx(int postReplyIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", postReplyIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_REPLY_USER_IDX, parameterSource, rs -> {
            int respondent = 0;
            if (rs.next()) {
                respondent = rs.getInt("userIdx");
            }

            return respondent;
        });
    }

    //userIdx, postReplyIdx로 답글 신고 유저 확인
    public boolean getPostReplyExistUser(int postReplyIdx, int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx",postReplyIdx)
                .addValue("userIdx", userIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_REPLY_EXIST_USER, parameterSource, rs -> {
            boolean reply_exist = false;
            if (rs.next()) {
                reply_exist = rs.getBoolean("user");
            }

            return reply_exist;
        });
    }

    //신고 3회 초과된 답글 삭제
    public ReportDto.PostReplyReportRes deletePostReply(ReportDto.DeletePostReply deletePostReply) {
        String qry = ReportSql.DELETE_POST_REPLY;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postReplyIdx", deletePostReply.getPostReplyIdx());
        namedParameterJdbcTemplate.update(qry, parameterSource);
        ReportDto.PostReplyReportRes deleteRes = new ReportDto.PostReplyReportRes(getRespondentByPostReplyIdx(deletePostReply.getPostReplyIdx()));
        return deleteRes;
    }






}
