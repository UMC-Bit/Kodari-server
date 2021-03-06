package com.bit.kodari.repository.postlike

class PostLikeSql {
    //토론장 게시글 좋아요/싫어요 선택
    public static final String CHOOSE_LIKE = """
        INSERT INTO PostLike (userIdx, postIdx, likeType)
        values (:userIdx, :postIdx, :likeType)
        """

//    public static final String GET_LIKE_CNT = """
//    SELECT COUNT(case when likeType = 1 then 1 end) as 'like', COUNT(case when likeType = 0 then 0 end) as 'dislike'
//    FROM PostLike
//    WHERE postIdx = :postIdx
//    """


    //postIdx로 게시글의 status 받아오기
    public static final String GET_POST_STATUS = """
        SELECT p.status FROM PostLike as l join Post as p on l.postIdx = p.postIdx
        WHERE l.postIdx = :postIdx
        """


    //postLikeIdx로 게시글의 userIdx 받아오기
    public static final String GET_USERIDX = """
        SELECT userIdx 
        FROM PostLike
        WHERE postLikeIdx = :postLikeIdx
        """

    //userIdx와 postIdx로 postLikeIdx 받아오기
    public static final String GET_POST_LIKE_IDX = """
        SELECT postLikeIdx
        FROM PostLike
        WHERE userIdx = :userIdx and postIdx = :postIdx and likeType = :likeType
        """

    //userIdx와 postCommentIdx로 유저가 또 들어가 있는지 확인
    public static final String EXIST_USER = """
        SELECT IF(userIdx, 'true', 'false' ) as 'exist'
        FROM PostLike
        WHERE userIdx = :userIdx and postIdx = :postIdx
        """

    //userIdx와 postCommentIdx로 유저가 또 들어가 있는지 확인
    public static final String EQUAL_LIKE = """
        SELECT likeType
        FROM PostLike
        WHERE userIdx = :userIdx and postIdx = :postIdx
        """

    //토론장 게시글 좋아요/싫어요 삭제
    public static final String DELETE_POST_LIKE = """
         DELETE FROM PostLike 
         WHERE postLikeIdx = :postLikeIdx
    """



//    //postLikeIdx로 status 받아오기
//    public static final String GET_LIKE_STATUS = """
//        SELECT status FROM PostLike WHERE postLikeIdx = :postLikeIdx
//        """
//
//    //postLikeIdx로 likeType 받아오기
//    public static final String GET_LIKE_TYPE = """
//        SELECT likeType FROM PostLike WHERE postLikeIdx = :postLikeIdx
//        """
//
//    //토론장 게시글 좋아요/싫어요 수정
//    public static final String UPDATE_POST_LIKE = """
//         UPDATE PostLike
//         SET likeType = :likeType
//         WHERE postIdx = :postIdx
//    """
//
//
//    //postLikeIdx로 userIdx 받아오기
//    public static final String GET_LIKE_USER_IDX = """
//        SELECT userIdx from PostLike WHERE postLikeIdx = :postLikeIdx
//        """
//
//    //postLikeIdx로 postIdx 받아오기
//    public static final String GET_LIKE_POST_IDX = """
//        SELECT postIdx from PostLike WHERE postLikeIdx = :postLikeIdx
//        """
//
//    //토론장 게시글별 좋아요 조회
//    public static final String LIST_POST_LIKE = """
//         SELECT COUNT(*) as 'true_cnt'
//         FROM PostLike as l
//            join (select postIdx, status from Post) as p on p.postIdx = l.postIdx
//         WHERE likeType = 1 and p.postIdx = :postIdx and p.status = 'active'
//         GROUP BY likeType
//         """
//
//    //토론장 게시글별 싫어요 조회
//    public static final String LIST_POST_DISLIKE = """
//         SELECT COUNT(*) as 'dislikeCnt'
//         FROM PostLike as l join Post as p on l.postIdx = p.postIdx
//         WHERE likeType = 0 and l.postIdx = :postIdx and p.status = 'active'
//         """




}
