package com.bit.kodari.repository.report;

import com.bit.kodari.dto.CommentLikeDto;
import com.bit.kodari.dto.ReportDto;
import com.bit.kodari.repository.commentlike.CommentLikeSql;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
                .addValue("postIdx", report.getPostIdx())
                .addValue("reporter", report.getReporter())
                .addValue("respondent", respondent);
        int affectedRows = namedParameterJdbcTemplate.update(reportSql.REPORT_POST, parameterSource, keyHolder);
        return new ReportDto.PostReportRes(report.getReporter(), keyHolder.getKey().intValue());
    }



    //postIdx로 userIdx 가져오기
    public int getUserIdxByPostIdx(int postIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", postIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_POST_USER_IDX, parameterSource, rs -> {
            int userIdx = 0;
            if (rs.next()) {
                userIdx = rs.getInt("userIdx");
            }

            return userIdx;
        });
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

    //postIdx로 신고당한유저(respondent) 가져오기
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
    public boolean getExistUser(int postIdx, int userIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx",postIdx)
                .addValue("userIdx", userIdx);
        return namedParameterJdbcTemplate.query(ReportSql.GET_EXIST_USER, parameterSource, rs -> {
            boolean exist = false;
            if (rs.next()) {
                exist = rs.getBoolean("user");
            }

            return exist;
        });
    }

    //신고 3회 초과된 게시글 삭제
    public ReportDto.PostReportRes deletePost(ReportDto.DeletePost deletePost) {
        String qry = ReportSql.DELETE_POST;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postIdx", deletePost.getPostIdx());
        namedParameterJdbcTemplate.update(qry, parameterSource);
        ReportDto.PostReportRes deleteRes = new ReportDto.PostReportRes(deletePost.getUserIdx(), commentLikeId);
        return deleteRes;
    }







}
