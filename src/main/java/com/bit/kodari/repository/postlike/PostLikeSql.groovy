package com.bit.kodari.repository.postlike

class PostLikeSql {
    //토론장 게시글 좋아요/싫어요 선택
    public static final String CHOOSE_LIKE = """
        INSERT INTO Postlike (userIdx, postIdx, likeType)
        values (:userIdx, :postIdx, :likeType)
        """

    //postLikeIdx로 userIdx 받아오기
    public static final String GET_LIKE_USER_IDX = """
        SELECT userIdx from Postlike WHERE postLikeIdx = :postLikeIdx 
        """

    //postLikeIdx로 postIdx 받아오기
    public static final String GET_LIKE_POST_IDX = """
        SELECT postIdx from Postlike WHERE postLikeIdx = :postLikeIdx 
        """


    //postIdx로 게시글의 status 받아오기
    public static final String GET_POST_STATUS = """
        SELECT p.status FROM Postlike as l join Post as p on l.postIdx = p.postIdx
        WHERE postIdx = :postIdx
        """

    //postLikeIdx로 status 받아오기
    public static final String GET_LIKE_STATUS = """
        SELECT status FROM Postlike WHERE postLikeIdx = :postLikeIdx
        """

    //postLikeIdx로 likeType 받아오기
    public static final String GET_LIKE_TYPE = """
        SELECT likeType FROM Postlike WHERE postLikeIdx = :postLikeIdx
        """


    //토론장 게시글 좋아요/싫어요 수정
    public static final String UPDATE_POST_LIKE = """
         UPDATE Postlike SET likeType = :likeType
         WHERE postIdx = :postIdx
    """

    //토론장 게시글 좋아요/싫어요 삭제
    public static final String DELETE_POST_LIKE = """
         DELETE FROM Postlike WHERE postIdx = :postIdx 
    """

    //토론장 게시글별 좋아요 조회
    public static final String LIST_POST_LIKE = """
         SELECT COUNT(likeType) as 'likeCnt'
         FROM Postlike
         WHERE likeType = 1 and postIdx = :postIdx
         """

    //토론장 게시글별 좋아요 조회
    public static final String LIST_POST_DISLIKE = """
         SELECT COUNT(likeType) as 'dislikeCnt'
         FROM Postlike
         WHERE likeType = 0 and postIdx = :postIdx
         """




}
