package com.bit.kodari.repository.board;

import com.bit.kodari.dto.BoardDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    BoardSql BoardSql;

    public BoardRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    //토론장 전체 카테고리 조회
    public List<BoardDto.GetBoardRes> getBoards(){
        SqlParameterSource parameterSource = new MapSqlParameterSource();
        List<BoardDto.GetBoardRes> getBoardRes = namedParameterJdbcTemplate.query(BoardSql.ALL_CATEGORY_BOARD, parameterSource,
                (rs, rowNum) -> new BoardDto.GetBoardRes(
                        rs.getString("boardName")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getBoardRes;
    }

    //토론장 카테고리별 조회
    public List<BoardDto.GetBoardRes> getBoardsByBoardIdx(int boardIdx) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("boardIdx", boardIdx);
        List<BoardDto.GetBoardRes> getBoardRes = namedParameterJdbcTemplate.query(BoardSql.CATEGORY_BOARD, parameterSource,
                (rs, rowNum) -> new BoardDto.GetBoardRes(
                        rs.getString("boardName")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        );

        return getBoardRes;
    }

}
