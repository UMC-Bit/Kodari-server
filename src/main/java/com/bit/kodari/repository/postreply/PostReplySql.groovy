package com.bit.kodari.repository.postreply

class PostReplySql {

    //토론장 게시글 댓글의 답글 등록
    public static final String INSERT_REPLY = """
        INSERT INTO PostReply (userIdx, postCommentIdx, content)
        values (:userIdx, :postCommentIdx, :content)
        """
    //postCommentIdx로 userIdx 받아오기
    public static final String GET_USER_IDX = """
        SELECT userIdx from PostReply WHERE postReplyIdx = :postReplyIdx 
        """

    //postCommentIdx로 userIdx 받아오기
    public static final String GET_COMMENT_IDX = """
        SELECT postCommentIdx from PostReply WHERE postReplyIdx = :postReplyIdx 
        """

    //postCommentIdx로 status 받아오기
    public static final String GET_COMMENT_STATUS = """
        SELECT c.status FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx 
        WHERE r.postCommentIdx = :postCommentIdx
        """

    //postCommentIdx로 status 받아오기
    public static final String GET_POST_STATUS = """
        SELECT DISTINCT p.status FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx
               join Post as p on c.postIdx = p.postIdx
        WHERE r.postCommentIdx = :postCommentIdx;
        """


    //유저의 신고 수 조회
    public static final String GET_REPORT_COUNT = """
        SELECT report AS 'report_count'
        FROM User
        WHERE userIdx = :userIdx
        """


    //토론장 게시글 답글 수정
    public static final String UPDATE_REPLY = """
         UPDATE PostReply SET content = :content
         WHERE postReplyIdx = :postReplyIdx and status = 'active'
    """

    //토론장 게시글 답글 삭제
    public static final String DELETE_REPLY = """
         UPDATE PostReply SET status = 'inactive' , content = '삭제된 답글입니다.' 
         WHERE postReplyIdx = :postReplyIdx and status = 'active'
    """

    //토론장 게시글 댓글별 답글조회
    public static final String LIST_COMMENT_REPLY = """
         SELECT u.profileImgUrl, u.nickName, r.content,
         case
                when timestampdiff(hour, r.updateAt, current_timestamp()) < 24 then date_format(r.updateAt, '%m/%d %H:%i')
                when timestampdiff(day, r.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, r.updateAt , NOW()), '일 전')
                when timestampdiff(month, r.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, r.updateAt , NOW()), '달 전')
                else CONCAT(TIMESTAMPDIFF(year, r.updateAt , NOW()), '년 전')
                end as time
         FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx join User as u on r.userIdx = u.userIdx 
         WHERE r.postCommentIdx = :postCommentIdx and c.status = 'active' and r.status = 'active'
         """

    //토론장 유저별 답글 조회
    public static final String LIST_USER_REPLY = """
         SELECT u.profileImgUrl, u.nickName, r.content,
         case
                when timestampdiff(hour, r.updateAt, current_timestamp()) < 24 then date_format(r.updateAt, '%m/%d %H:%i')
                when timestampdiff(day, r.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, r.updateAt , NOW()), '일 전')
                when timestampdiff(month, r.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, r.updateAt , NOW()), '달 전')
                else CONCAT(TIMESTAMPDIFF(year, r.updateAt , NOW()), '년 전')
                end as time
         FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx join User as u on r.userIdx = u.userIdx 
         WHERE r.userIdx = :userIdx and c.status = 'active' and r.status = 'active'
         """

    //토론장 댓글별 답글 수 조회
    public static final String LIST_REPLY_CNT = """
         SELECT COUNT(*) as 'reply_cnt'
         FROM PostReply as r
            join PostComment as c on r.postCommentIdx = c.postCommentIdx
         WHERE r.postCommentIdx = :postCommentIdx and c.status = 'active' and r.status = 'active'
         """
}
