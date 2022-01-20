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

    //토론장 게시글 답글 수정
    public static final String UPDATE_REPLY = """
         UPDATE PostReply SET content = :content
         WHERE postReplyIdx = :postReplyIdx and status = 'active'
    """

    //토론장 게시글 답글 삭제
    public static final String DELETE_REPLY = """
         UPDATE PostReply SET status = 'inactive' WHERE postReplyIdx = :postReplyIdx and status = 'active'
    """

    //토론장 게시글 댓글별 답글조회
    public static final String LIST_COMMENT_REPLY = """
         SELECT u.nickName, r.content
         FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx join User as u on r.userIdx = u.userIdx 
         WHERE r.postCommentIdx = :postCommentIdx and c.status = 'active' and r.status = 'active'
         """

    //토론장 유저별 답글 조회
    public static final String LIST_USER_REPLY = """
         SELECT u.nickName, r.content
         FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx join User as u on r.userIdx = u.userIdx 
         WHERE r.userIdx = :userIdx and c.status = 'active' and r.status = 'active'
         """
}
