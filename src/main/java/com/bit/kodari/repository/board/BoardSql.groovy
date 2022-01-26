package com.bit.kodari.repository.board

class BoardSql {

    //토론장 카테고리 전체 조회
    public static final String ALL_CATEGORY_BOARD = """
         SELECT boardName
         FROM Board  
         """

    //토론장 카테고리별 조회
    public static final String CATEGORY_BOARD = """
         SELECT boardName
         FROM Board 
         WHERE boardIdx = :boardIdx
         """
}
