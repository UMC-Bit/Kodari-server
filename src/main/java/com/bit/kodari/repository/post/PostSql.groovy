package com.bit.kodari.repository.post

class PostSql {

    //토론장 게시글 등록
    public static final String INSERT_POST = """
        INSERT INTO Post (boardIdx, userIdx, content)
        values (:boardIdx, :userIdx, :content)
        """

    //postIdx로 userIdx 받아오기
    public static final String GET_USER_IDX = """
        SELECT userIdx from Post WHERE postIdx = :postIdx 
        """


    //postIdx로 postCommentIdx, postReplyIdx, postLikeIdx 받아오기
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

    //postIdx로 postLikeIdx 받아오기
    public static final String GET_LIKE_IDX = """
        SELECT postLikeIdx
        FROM PostLike as l join Post as p on l.postIdx = p.postIdx
        WHERE p.postIdx = :postIdx
        """

    //postIdx로 status 받아오기
    public static final String GET_STATUS = """
        SELECT status FROM Post WHERE postIdx = :postIdx
        """


    //토론장 게시글 수정
    public static final String UPDATE_POST = """
         UPDATE Post SET  content = :content
         WHERE postIdx = :postIdx
    """

    //토론장 게시글 삭제
    public static final String DELETE_POST = """
         UPDATE Post SET status = 'inactive' WHERE postIdx = :postIdx
    """

    //토론장 게시글 댓글 삭제
    public static final String DELETE_COMMENT = """
         UPDATE PostComment
         SET status = 'inactive' WHERE postCommentIdx = :postCommentIdx
    """

    //토론장 게시글 좋아요/싫어요 삭제
    public static final String DELETE_LIKE = """
         DELETE FROM PostLike
         WHERE postLikeIdx = :postLikeIdx
    """

    //토론장 게시글 답글 삭제
    public static final String DELETE_REPLY = """
         UPDATE PostReply
         SET status = 'inactive' WHERE postReplyIdx = :postReplyIdx
    """


    //토론장 게시글 조회
    public static final String LIST_POST = """
         SELECT b.boardName, u.nickName, content, p.status 
         FROM Post as p join Board as b on p.boardIdx = b.boardIdx join User as u on p.userIdx = u.userIdx 
         WHERE p.status = 'active'
         """

    //토론장 유저 게시글 조회
    public static final String LIST_USER_POST = """
         SELECT b.boardName, u.nickName, content, p.status
         FROM Post as p join Board as b on p.boardIdx = b.boardIdx join User as u on p.userIdx = u.userIdx 
         WHERE p.userIdx = :userIdx and p.status = 'active'
         """







}
