package com.bit.kodari.service;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.CommentLikeDto;
import com.bit.kodari.repository.commentlike.CommentLikeRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class CommentLikeService {
    @Autowired
    CommentLikeRepository commentLikeRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public CommentLikeService(CommentLikeRepository commentLikeRepository, JwtService jwtService) {
        this.commentLikeRepository = commentLikeRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // 토론장 댓글 좋아요 선택(POST)
    @Transactional
    public CommentLikeDto.CommentLikeRes chooseCommentLike(CommentLikeDto.RegisterCommentLikeReq registerCommentLikeReq) throws BaseException {
        int postCommentIdx = registerCommentLikeReq.getPostCommentIdx();
        int userIdx = registerCommentLikeReq.getUserIdx();
        int commentLikeIdx = commentLikeRepository.getCommentLikeIdxByIdx(userIdx, postCommentIdx);
        String exist_user = commentLikeRepository.getUser(userIdx, postCommentIdx);
        //유저가 있으면 1반환
        String status = commentLikeRepository.getStatusByPostCommentIdx(postCommentIdx);
        if (status.equals("inactive")) { //삭제된 댓글이면 선택불가
            throw new BaseException(IMPOSSIBLE_POST_COMMENT);
        }
        try {
            return commentLikeRepository.chooseCommentLike(registerCommentLikeReq);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //토론장 게시글 좋아요/싫어요 삭제
    @Transactional
    public CommentLikeDto.CommentLikeRes deleteLike(CommentLikeDto.CommentLikeReq delete) throws BaseException {
        try {
            CommentLikeDto.CommentLikeRes deleteLikeRes = commentLikeRepository.deleteLike(delete);
            if (deleteLikeRes.getCommentLikeIdx() == 0) {
                throw new BaseException(DELETE_FAIL_COMMENT_LIKE);
            }
            return deleteLikeRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
