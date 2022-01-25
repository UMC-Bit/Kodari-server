package com.bit.kodari.repository.board

class BoardSql {

    //토론장 게시글 조회
    public static final String ALL_CATEGORY_BOARD = """
         SELECT boardName
         FROM Board  
         """

    //토론장 유저 게시글 조회
    public static final String CATEGORY_BOARD = """
         SELECT boardName
         FROM Board 
         WHERE boardIdx = :boardIdx
         """
}
