package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.BoardDto;
import com.bit.kodari.repository.board.BoardRepository;
import com.bit.kodari.service.BoardService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/boards")
public class BoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    BoardRepository boardRepository;


    /*
        토론장 카테고리 조회
     */
    @GetMapping("") // (GET) 127.0.0.1:9000/boards
    @ApiOperation(value = "카테고리 조회", notes = "토론장 카테고리 조회함")
    public BaseResponse<List<BoardDto.GetBoardRes>> getBoards(@RequestParam(required = false) Integer boardIdx) {
        try {
            if (boardIdx == null) {
                List<BoardDto.GetBoardRes> getBoardsRes = boardService.getBoards();
                return new BaseResponse<>(getBoardsRes);
            }
            List<BoardDto.GetBoardRes> getBoardsRes = boardService.getBoardsByBoardIdx(boardIdx);
            return new BaseResponse<>(getBoardsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
