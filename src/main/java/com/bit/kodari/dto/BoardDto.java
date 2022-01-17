package com.bit.kodari.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BoardDto {

    //토론장 기본정보
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Board {
        private int boardIdx;
        private String boardName;
        private String status;
    }

    //토론장 카테고리 조회
    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetBoardRes{
        private String boardName;
    }

}
