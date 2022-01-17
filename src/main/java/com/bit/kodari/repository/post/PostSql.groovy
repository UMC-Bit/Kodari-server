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

    //토론장 게시글 조회
    public static final String LIST_POST = """
         SELECT b.boardName, u.nickName, content, p.status 
         FROM Post as p join Board as b on p.boardIdx = b.boardIdx join User as u on p.userIdx = u.userIdx 
         """

    //토론장 유저 게시글 조회
    public static final String LIST_USER_POST = """
         SELECT b.boardName, u.nickName, content, p.status
         FROM Post as p join Board as b on p.boardIdx = b.boardIdx join User as u on p.userIdx = u.userIdx 
         WHERE p.userIdx = :userIdx
         """







}
