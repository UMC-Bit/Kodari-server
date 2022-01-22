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

    //postIdx로 postIdx 받아오기
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

    //postIdx로 userIdx 받아오기
    public static final String GET_COMMENT_USER_IDX = """
        SELECT userIdx
        FROM PostComment
        WHERE postIdx = :postIdx
        """

    //postCommentIdx로 commentLikeIdx 받아오기
    public static final String GET_COMMENT_LIKE_IDX = """
        SELECT cl.commentLikeIdx
        FROM CommentLike as cl join PostComment as c on cl.postCommentIdx = c.postCommentIdx
        WHERE c.postCommentIdx = :postCommentIdx
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

    //삭제된 댓글과 관련된 댓글 좋아요 삭제
    public static final String DELETE_COMMENT_LIKE = """
         DELETE FROM CommentLike
         WHERE commentLikeIdx = :commentLikeIdx
    """

    //토론장 게시글별 댓글 조회
    public static final String LIST_POST_COMMENT = """
         SELECT u.nickName,  u.profileImgUrl , c.content,  count(case when cl.like = 1 then 1 end) as 'like'
         FROM PostComment as c join Post as p on c.postIdx = p.postIdx join User as u on c.userIdx = u.userIdx
         LEFT join CommentLike as cl on cl.postCommentIdx = c.postCommentIdx
         WHERE c.postIdx = :postIdx and c.status = 'active' and p.status = 'active'
         GROUP BY u.nickName,  u.profileImgUrl , c.content
         """

    //토론장 유저별 게시글 조회
    public static final String LIST_USER_COMMENT = """
         SELECT u.nickName,  u.profileImgUrl , c.content,  count(case when cl.like = 1 then 1 end) as 'like'
         FROM PostComment as c join Post as p on c.postIdx = p.postIdx join User as u on c.userIdx = u.userIdx
         LEFT join CommentLike as cl on cl.postCommentIdx = c.postCommentIdx
         WHERE c.userIdx = :userIdx and p.status = 'active' and c.status = 'active'
         GROUP BY u.nickName,  u.profileImgUrl , c.content
         """

    //토론장 게시글별 댓글 수 조회
    public static final String LIST_COMMENT_CNT = """
         SELECT COUNT(*) as 'comment_cnt'
         FROM PostComment as c
            join Post as p on c.postIdx = p.postIdx
         WHERE c.postIdx = :postIdx and p.status = 'active' and c.status = 'active'
         """


}
