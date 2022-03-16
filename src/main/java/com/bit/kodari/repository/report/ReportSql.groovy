package com.bit.kodari.repository.report

class ReportSql {
    //토론장 게시글 신고
    public static final String REPORT_POST = """
        INSERT INTO PostReport (postIdx, reporter, respondent)
        values (:postIdx, :reporter, :respondent)
        """

    //postIdx로 userIdx 받아오기
    public static final String GET_POST_USER_IDX = """
        SELECT userIdx FROM Post WHERE postIdx = :postIdx
        """

    //postReport 테이블에 해당 게시글의 신고횟수 세기
    public static final String GET_POST_REPORT_CNT = """
        SELECT COUNT(ifnull(postReportIdx,0)) AS 'postReportCnt' FROM PostReport WHERE postIdx = :postIdx
        """

    //해당 게시글을 신고한 유저 존재 여부 가져오기 -> userIdx와 reporter 오류날 가능성 있음
    public static final String GET_EXIST_USER = """
    SELECT case when COUNT(ifnull(postReportIdx,0)) = 1 then true when COUNT(ifnull(postReportIdx,0)) = 0 then false end as 'user'
    FROM PostReport
    WHERE postIdx = :postIdx and reporter = :userIdx
    """

}
