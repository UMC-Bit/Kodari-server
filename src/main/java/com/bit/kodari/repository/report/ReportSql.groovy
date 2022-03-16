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
    public static final String GET_POST_EXIST_USER = """
    SELECT case when COUNT(ifnull(postReportIdx,0)) = 1 then true when COUNT(ifnull(postReportIdx,0)) = 0 then false end as 'user'
    FROM PostReport
    WHERE postIdx = :postIdx and reporter = :userIdx
    """

    //토론장 게시글 삭제
    public static final String DELETE_POST = """
         UPDATE Post SET status = 'inactive' WHERE postIdx = :postIdx
    """

    //토론장 게시글 삭제 시 유저 report + 1
    public static final String ADD_USER_REPORT = """
        UPDATE User SET report = report + 1 WHERE userIdx = :userIdx 
   """

    //삭제된 게시글과 관련된 댓글 삭제
    public static final String POST_DELETE_COMMENT = """
         UPDATE PostComment
         SET status = 'inactive' WHERE postCommentIdx = :postCommentIdx
    """

    //삭제된 게시글과 관련된 댓글 좋아요 삭제
    public static final String POST_DELETE_COMMENT_LIKE = """
         DELETE FROM CommentLike
         WHERE commentLikeIdx = :commentLikeIdx
    """

    //삭제된 게시글과 관련된 좋아요/싫어요 삭제
    public static final String POST_DELETE_LIKE = """
         DELETE FROM PostLike
         WHERE postLikeIdx = :postLikeIdx
    """

    //삭제된 게시글과 관련된 답글 삭제
    public static final String POST_DELETE_REPLY = """
         UPDATE PostReply
         SET status = 'inactive' WHERE postReplyIdx = :postReplyIdx
    """


    //postIdx로 postCommentIdx 받아오기
    public static final String GET_COMMENT_IDX = """
        SELECT c.postCommentIdx
        FROM PostComment as c join Post as p on c.postIdx = p.postIdx 
        WHERE p.postIdx = :postIdx 
        """

    //postIdx로 postReplyIdx 받아오기
    public static final String GET_REPLY_IDX = """
        SELECT postReplyIdx
        FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx
        join Post as p on c.postIdx = p.postIdx
        WHERE p.postIdx = :postIdx
        """

    //postIdx로 commentLikeIdx 받아오기
    public static final String GET_COMMENT_LIKE_IDX = """
        SELECT cl.commentLikeIdx
        FROM CommentLike as cl join PostComment as c on cl.postCommentIdx = c.postCommentIdx
        join Post as p on c.postIdx = p.postIdx
        WHERE p.postIdx = :postIdx
        """

    //postIdx로 postLikeIdx 받아오기
    public static final String GET_LIKE_IDX = """
        SELECT postLikeIdx
        FROM PostLike as l join Post as p on l.postIdx = p.postIdx
        WHERE p.postIdx = :postIdx
        """



    //댓글 신고


    //토론장 댓글 신고
    public static final String REPORT_POST_COMMENT = """
        INSERT INTO PostCommentReport (postCommentIdx, reporter, respondent)
        values (:postCommentIdx, :reporter, :respondent)
        """

    //postCommentIdx로 userIdx 받아오기
    public static final String GET_POST_COMMENT_USER_IDX = """
        SELECT userIdx FROM PostComment WHERE postCommentIdx = :postCommentIdx
        """

    //postCommentReport 테이블에 해당 게시글의 신고횟수 세기
    public static final String GET_POST_COMMENT_REPORT_CNT = """
        SELECT COUNT(ifnull(postCommentReportIdx,0)) AS 'postCommentReportCnt' FROM PostCommentReport WHERE postCommentIdx = :postCommentIdx
        """

    //해당 댓글을 신고한 유저 존재 여부 가져오기 -> userIdx와 reporter 오류날 가능성 있음
    public static final String GET_POST_COMMENT_EXIST_USER = """
        SELECT case when COUNT(ifnull(postCommentReportIdx,0)) = 1 then true when COUNT(ifnull(postCommentReportIdx,0)) = 0 then false end as 'user'
        FROM PostCommentReport
        WHERE postCommentIdx = :postCommentIdx and reporter = :userIdx
    """

    //토론장 댓글 삭제
    public static final String DELETE_POST_COMMENT = """
         UPDATE PostComment 
         SET status = 'inactive', content = '운영원칙에 위배된 댓글입니다.' 
         WHERE postCommentIdx = :postCommentIdx
    """


    //토론장 답글 신고


    //토론장 답글 신고
    public static final String REPORT_POST_REPLY = """
        INSERT INTO PostReplyReport (postReplyIdx, reporter, respondent)
        values (:postReplyIdx, :reporter, :respondent)
        """

    //postReplyIdx로 userIdx 받아오기
    public static final String GET_POST_REPLY_USER_IDX = """
        SELECT userIdx FROM PostReply WHERE postReplyIdx = :postReplyIdx
        """

    //postReplyReport 테이블에 해당 답글의 신고횟수 세기
    public static final String GET_POST_REPLY_REPORT_CNT = """
        SELECT COUNT(ifnull(postReplyReportIdx,0)) AS 'postReplyReportCnt' FROM PostReplyReport WHERE postReplyIdx = :postReplyIdx
        """

    //해당 답글을 신고한 유저 존재 여부 가져오기 -> userIdx와 reporter 오류날 가능성 있음
    public static final String GET_POST_REPLY_EXIST_USER = """
    SELECT case when COUNT(ifnull(postReplyReportIdx,0)) = 1 then true when COUNT(ifnull(postReplyReportIdx,0)) = 0 then false end as 'user'
    FROM PostReplyReport
    WHERE postReplyIdx = :postReplyIdx and reporter = :userIdx
    """

    //토론장 답글 삭제
    public static final String DELETE_POST_REPLY = """
         UPDATE PostReply 
         SET status = 'inactive', content = '운영원칙에 위배된 댓글입니다.'
         WHERE postReplyIdx = :postReplyIdx
    """


}
