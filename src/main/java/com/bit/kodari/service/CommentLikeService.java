package com.bit.kodari.service;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.CommentLikeDto;
import com.bit.kodari.repository.commentlike.CommentLikeRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public CommentLikeDto.RegisterCommentLikeRes chooseCommentLike(CommentLikeDto.RegisterCommentLikeReq registerCommentLikeReq) throws BaseException {
        int postCommentIdx = registerCommentLikeReq.getPostCommentIdx();
        String status = commentLikeRepository.getStatusByPostCommentIdx(postCommentIdx);
        if(status.equals("inactive")) { //삭제된 댓글이면 선택불가
            throw new BaseException(IMPOSSIBLE_POST_COMMENT);
        }

        try {
            return commentLikeRepository.chooseCommentLike(registerCommentLikeReq);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 게시글 좋아요/싫어요 삭제
    public void deleteLike(CommentLikeDto.DeleteLikeReq delete) throws BaseException {
        int commentLikeIdx = delete.getCommentLikeIdx();
        int like = delete.getLike();
        int equal_like = commentLikeRepository.getLikeByCommentLikeIdx(commentLikeIdx);
        if(like == equal_like) { //같은 타입을 고르면 삭제
            int result = commentLikeRepository.deleteLike(delete);
            if (result == 0) {
                throw new BaseException(DELETE_FAIL_COMMENT_LIKE);
            }
        }
        try {


        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }

}
