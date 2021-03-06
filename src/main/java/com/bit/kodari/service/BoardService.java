package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.BoardDto;
import com.bit.kodari.repository.board.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
public class BoardService {
    @Autowired
    BoardRepository boardRepository;

    // 토론장 전체 카테고리 조회
    public List<BoardDto.GetBoardRes> getBoards() throws BaseException {
        try {
            List<BoardDto.GetBoardRes> getBoardRes = boardRepository.getBoards();
            return getBoardRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 유저의 게시글 조회
    public List<BoardDto.GetBoardRes> getBoardsByBoardIdx(int boardIdx) throws BaseException {
        try {
            List<BoardDto.GetBoardRes> getBoardsRes = boardRepository.getBoardsByBoardIdx(boardIdx);
            return getBoardsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
