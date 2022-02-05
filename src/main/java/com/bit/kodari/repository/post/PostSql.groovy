package com.bit.kodari.repository.post

class PostSql {

    //토론장 게시글 등록
    public static final String INSERT_POST = """
        INSERT INTO Post (coinIdx, userIdx, content)
        values (:coinIdx, :userIdx, :content)
        """

    //postIdx로 userIdx 받아오기
    public static final String GET_USER_IDX = """
        SELECT userIdx from Post WHERE postIdx = :postIdx 
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

    //postIdx로 status 받아오기
    public static final String GET_STATUS = """
        SELECT status FROM Post WHERE postIdx = :postIdx
        """



    //토론장 게시글 수정
    public static final String UPDATE_POST = """
         UPDATE Post SET content = :content
         WHERE postIdx = :postIdx
    """

    //토론장 게시글 삭제
    public static final String DELETE_POST = """
         UPDATE Post SET status = 'inactive' WHERE postIdx = :postIdx
    """

    //삭제된 게시글과 관련된 댓글 삭제
    public static final String DELETE_COMMENT = """
         UPDATE PostComment
         SET status = 'inactive' WHERE postCommentIdx = :postCommentIdx
    """

    //삭제된 게시글과 관련된 댓글 좋아요 삭제
    public static final String DELETE_COMMENT_LIKE = """
         DELETE FROM CommentLike
         WHERE commentLikeIdx = :commentLikeIdx
    """

    //삭제된 게시글과 관련된 좋아요/싫어요 삭제
    public static final String DELETE_LIKE = """
         DELETE FROM PostLike
         WHERE postLikeIdx = :postLikeIdx
    """

    //삭제된 게시글과 관련된 답글 삭제
    public static final String DELETE_REPLY = """
         UPDATE PostReply
         SET status = 'inactive' WHERE postReplyIdx = :postReplyIdx
    """


    //토론장 게시글 조회
    public static final String LIST_POST = """
        SELECT p.postIdx, c.symbol, u.nickName, u.profileImgUrl, p.content, count(case when l.likeType = 1 then 1 end) as 'like', count(case when l.likeType = 0 then 0 end) as 'dislike',
        case
                when timestampdiff(hour, p.updateAt, current_timestamp()) < 24 then date_format(p.updateAt, '%m/%d %H:%i')
                when timestampdiff(day, p.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, p.updateAt , NOW()), '일 전')
                when timestampdiff(month, p.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, p.updateAt , NOW()), '달 전')
                else CONCAT(TIMESTAMPDIFF(year, p.updateAt , NOW()), '년 전')
                end as time
        FROM Post as p join Coin as c on p.coinIdx = c.coinIdx join User as u on p.userIdx = u.userIdx
        Left join PostLike as l on l.postIdx = p.postIdx
        WHERE p.status = 'active'
        group by c.symbol, u.nickName, u.profileImgUrl, p.content
        ORDER BY p.postIdx DESC
         """

    //토론장 유저 게시글 조회
    public static final String LIST_USER_POST = """
         SELECT p.postIdx, c.symbol, u.nickName, u.profileImgUrl, p.content, count(case when l.likeType = 1 then 1 end) as 'like', count(case when l.likeType = 0 then 0 end) as 'dislike',
         case
                when timestampdiff(hour, p.updateAt, current_timestamp()) < 24 then date_format(p.updateAt, '%m/%d %H:%i')
                when timestampdiff(day, p.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, p.updateAt , NOW()), '일 전')
                when timestampdiff(month, p.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, p.updateAt , NOW()), '달 전')
                else CONCAT(TIMESTAMPDIFF(year, p.updateAt , NOW()), '년 전')
                end as time
         FROM Post as p join Coin as c on p.coinIdx = c.coinIdx join User as u on p.userIdx = u.userIdx 
            Left join PostLike as l on l.postIdx = p.postIdx
         WHERE p.userIdx = :userIdx and p.status = 'active'
         group by c.symbol, u.nickName, u.profileImgUrl, p.content
         ORDER BY p.postIdx DESC
         """

    //토론장 코인 게시글 조회
    public static final String LIST_COIN_POST = """
        SELECT p.postIdx, c.symbol, u.nickName, u.profileImgUrl, p.content, count(case when l.likeType = 1 then 1 end) as 'like', count(case when l.likeType = 0 then 0 end) as 'dislike',
        case
           when timestampdiff(hour, p.updateAt, current_timestamp()) < 24 then date_format(p.updateAt, '%m/%d %H:%i')
           when timestampdiff(day, p.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, p.updateAt , NOW()), '일 전')
           when timestampdiff(month, p.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, p.updateAt , NOW()), '달 전')
           else CONCAT(TIMESTAMPDIFF(year, p.updateAt , NOW()), '년 전')
           end as 'time'
         FROM Post as p join Coin as c on p.coinIdx = c.coinIdx join User as u on p.userIdx = u.userIdx
            Left join PostLike as l on l.postIdx = p.postIdx
         WHERE c.coinName = :coinName and p.status = 'active'
         group by c.symbol, u.nickName, u.profileImgUrl, p.content, c.coinName
         ORDER BY p.postIdx DESC
         """

    //postIdx로 댓글 수 조회
    public static final String GET_COMMENT_COUNT = """
        SELECT COUNT(ifnull(c.postCommentIdx,0)) AS 'comment_count'
        FROM PostComment as c join Post as p on c.postIdx = p.postIdx
        WHERE p.postIdx = :postIdx and c.status = 'active'
        """

    //토론장 게시글별 게시글 조회
    public static final String LIST_POSTS = """
         SELECT p.postIdx, c.symbol, u.nickName, u.profileImgUrl, p.content, count(case when l.likeType = 1 then 1 end) as 'like', count(case when l.likeType = 0 then 0 end) as 'dislike',
       case
           when timestampdiff(hour, p.updateAt, current_timestamp()) < 24 then date_format(p.updateAt, '%m/%d %H:%i')
           when timestampdiff(day, p.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, p.updateAt , NOW()), '일 전')
           when timestampdiff(month, p.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, p.updateAt , NOW()), '달 전')
           else CONCAT(TIMESTAMPDIFF(year, p.updateAt , NOW()), '년 전')
           end as 'time'
        FROM Post as p join Coin as c on p.coinIdx = c.coinIdx join User as u on p.userIdx = u.userIdx
               Left join PostLike as l on l.postIdx = p.postIdx
        WHERE p.postIdx = :postIdx and p.status = 'active'
        group by c.symbol, u.nickName, u.profileImgUrl, p.content
         """

    //토론장 게시글별 댓글 조회
    public static final String LIST_COMMENT = """
        SELECT c.userIdx, c.postCommentIdx, u.profileImgUrl, u.nickName, c.content, count(case when cl.like = 1 then 1 end) as 'like',
        case
                when timestampdiff(hour, c.updateAt, current_timestamp()) < 24 then date_format(c.updateAt, '%m/%d %H:%i')
                when timestampdiff(day, c.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, c.updateAt , NOW()), '일 전')
                when timestampdiff(month, c.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, c.updateAt , NOW()), '달 전')
                else CONCAT(TIMESTAMPDIFF(year, c.updateAt , NOW()), '년 전')
                end as 'time'
        FROM PostComment as c join User as u on c.userIdx = u.userIdx join Post as p on c.postIdx = p.postIdx
            LEFT join CommentLike as cl on c.postCommentIdx = cl.postCommentIdx
        WHERE c.postIdx = :postIdx
        GROUP BY c.userIdx, c.postCommentIdx, u.profileImgUrl, u.nickName, c.content
        """

    //게시글의 달린 댓글의 status 가져오기
    public static final String GET_POST_COMMENT_STATUS = """
    SELECT c.status AS 'comment_status'
    FROM PostComment as c join Post as p on c.postIdx = p.postIdx
    WHERE c.userIdx = :userIdx and p.postIdx = :postIdx
    """

    //댓글 좋아요 유저 존재 여부 가져오기
    public static final String GET_COMMENT_LIKE = """
    SELECT case when COUNT(ifnull(commentLikeIdx,0)) = 1 then true when COUNT(ifnull(commentLikeIdx,0)) = 0 then false end as 'commentLike'
    FROM CommentLike
    WHERE userIdx = :userIdx and postCommentIdx = :postCommentIdx
    """



    // 토론장 commentIdx로 답글 조회
    public static final String LIST_REPLY_BY_COMMENT_ID = """
        SELECT r.userIdx, r.postReplyIdx, u.profileImgUrl, u.nickName, r.content,
        case
                when timestampdiff(hour, r.updateAt, current_timestamp()) < 24 then date_format(r.updateAt, '%m/%d %H:%i')
                when timestampdiff(day, r.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, r.updateAt , NOW()), '일 전')
                when timestampdiff(month, r.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, r.updateAt , NOW()), '달 전')
                else CONCAT(TIMESTAMPDIFF(year, r.updateAt , NOW()), '년 전')
                end as 'time'
        FROM PostReply as r join User as u on r.userIdx = u.userIdx
                    join PostComment as c on r.postCommentIdx = c.postCommentIdx
                    join Post as p on c.postIdx = p.postIdx
        WHERE r.postCommentIdx = :postCommentIdx
    """

    //게시글의 댓글에 달린 답글의 status 가져오기
    public static final String GET_COMMENT_REPLY_STATUS = """
    SELECT r.status as 'reply_status'
    FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx
    WHERE r.userIdx = :userIdx and c.postCommentIdx = :postCommentIdx
    """

//    //토론장 게시글별 댓글의 답글 조회
//    public static final String LIST_REPLY = """
//        SELECT u.profileImgUrl, u.nickName, r.content,
//        case
//                when timestampdiff(hour, r.updateAt, current_timestamp()) < 24 then date_format(r.updateAt, '%m/%d %H:%i')
//                when timestampdiff(day, r.updateAt, current_timestamp()) < 30 then CONCAT(TIMESTAMPDIFF(day, r.updateAt , NOW()), '일 전')
//                when timestampdiff(month, r.updateAt, current_timestamp()) < 12 then CONCAT(TIMESTAMPDIFF(month, r.updateAt , NOW()), '달 전')
//                else CONCAT(TIMESTAMPDIFF(year, r.updateAt , NOW()), '년 전')
//                end as 'time'
//        FROM PostReply as r join User as u on r.userIdx = u.userIdx
//                    join PostComment as c on r.postCommentIdx = c.postCommentIdx
//                    join Post as p on c.postIdx = p.postIdx
//        WHERE p.postIdx = :postIdx
//        """
//    //postCommentIdx로 status 받아오기
//    public static final String GET_COMMENT_STATUS = """
//        SELECT status FROM PostComment WHERE postCommentIdx = :postCommentIdx
//        """
//
//    //postReplyIdx로 status 받아오기
//    public static final String GET_REPLY_STATUS = """
//        SELECT status FROM PostReply WHERE postReplyIdx = :postReplyIdx
//        """
//
//
//    //postIdx로 userIdx 받아오기
//    public static final String GET_COMMENT_USER_IDX = """
//        SELECT userIdx FROM PostComment WHERE postIdx = :postIdx
//        """
//
//    //postReplyIdx로 userIdx 받아오기
//    public static final String GET_REPLY_USER_IDX = """
//        SELECT r.userIdx FROM PostReply as r join PostComment as c on r.postCommentIdx = c.postCommentIdx
//            join Post as p on c.postIdx = p.postIdx
//        WHERE p.postIdx = :postIdx
//        """



}
