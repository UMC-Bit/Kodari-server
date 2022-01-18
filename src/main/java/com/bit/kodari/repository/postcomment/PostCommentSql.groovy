package com.bit.kodari.repository.postcomment

class PostCommentSql {

    //토론장 게시글 댓글 등록
    public static final String INSERT_COMMENT = """
        INSERT INTO PostComment (userIdx, postIdx, content)
        values (:userIdx, :postIdx, :content)
        """

    //postCommentIdx로 userIdx 받아오기
    public static final String GET_USER_IDX = """
        SELECT userIdx from PostComment WHERE postCommentIdx = :postCommentIdx 
        """

    //postIdx로 userIdx 받아오기
    public static final String GET_POST_IDX = """
        SELECT postIdx from PostComment WHERE postCommentIdx = :postCommentIdx 
        """

    //postCommentIdx로 status 받아오기
    public static final String GET_STATUS = """
        SELECT status FROM PostComment WHERE postCommentIdx = :postCommentIdx
        """

    //postIdx로 댓글쓴 게시글의 status 받아오기
    public static final String GET_POST_STATUS ="""
        SELECT p.status FROM PostComment as c join Post as p on c.postIdx = p.postIdx
        WHERE c.postIdx = :postIdx
        """

    //토론장 게시글 수정
    public static final String UPDATE_COMMENT = """
         UPDATE PostComment SET content = :content
         WHERE postCommentIdx = :postCommentIdx
    """

    //토론장 게시글 삭제
    public static final String DELETE_COMMENT = """
         UPDATE PostComment SET status = 'inactive' WHERE postCommentIdx = :postCommentIdx
    """

    //토론장 게시글 댓글 조회
    public static final String LIST_POST_COMMENT = """
         SELECT p.boardIdx, u.nickName,  c.likeCnt, c.content
         FROM PostComment as c join Post as p on c.postIdx = p.postIdx join User as u on c.userIdx = u.userIdx
         WHERE c.postIdx = :postIdx and c.status = 'active'
         """

    //토론장 유저 게시글 조회
    public static final String LIST_USER_COMMENT = """
         SELECT p.boardIdx, u.nickName, c.likeCnt, c.content
         FROM PostComment as c join Post as p on c.postIdx = p.postIdx join User as u on c.userIdx = u.userIdx 
         WHERE c.userIdx = :userIdx and p.status = 'active' and c.status = 'active'
         """




}
