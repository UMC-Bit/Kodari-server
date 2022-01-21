package com.bit.kodari.repository.commentlike

class CommentLikeSql {
    //토론장 댓글 좋아요 선택
    public static final String CHOOSE_COMMENT_LIKE = """
        INSERT INTO CommentLike (userIdx, postCommentIdx)
        values (:userIdx, :postCommentIdx)
        """

    //commentLikeIdx로 userIdx 받아오기
    public static final String GET_LIKE_USER_IDX = """
        SELECT userIdx from CommentLike WHERE commentLikeIdx = :commentLikeIdx 
        """

    //commentLikeIdx로 postCommentIdx 받아오기
    public static final String GET_LIKE_POST_IDX = """
        SELECT postCommentIdx from CommentLike WHERE commentLikeIdx = :commentLikeIdx 
        """

    //postCommentIdx로 게시글의 status 받아오기
    public static final String GET_POST_STATUS = """
        SELECT p.status FROM PostComment as c join Post as p on c.postIdx = p.postIdx
        WHERE c.postCommentIdx = :postCommentIdx
        """

    //postCommentIdx로 댓글의 status 받아오기
    public static final String GET_COMMENT_STATUS = """
        SELECT status FROM PostComment WHERE postCommentIdx = :postCommentIdx
        """

    //commentLikeIdx로 like 받아오기
    public static final String GET_LIKE = """
        SELECT CommentLike.like
        FROM CommentLike
        WHERE commentLikeIdx = :commentLikeIdx
        """

    //토론장 게시글 좋아요/싫어요 삭제
    public static final String DELETE_COMMENT_LIKE = """
        DELETE FROM CommentLike
        WHERE commentLikeIdx = :commentLikeIdx and CommentLike.like = :like
        """
}
