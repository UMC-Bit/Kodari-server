package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.PostCommentDto;
import com.bit.kodari.repository.postcomment.PostCommentRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class PostCommentService {
    @Autowired
    PostCommentRepository postCommentRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public PostCommentService(PostCommentRepository postCommentRepository, JwtService jwtService) {
        this.postCommentRepository = postCommentRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }


    // 토론장 게시글 댓글 등록 (POST)
    public PostCommentDto.RegisterCommentRes insertPostComment(PostCommentDto.RegisterCommentReq registerCommentReq) throws BaseException {
        int postIdx = registerCommentReq.getPostIdx();
        String post_status = postCommentRepository.getStatusByPostIdx(postIdx);
        String content = registerCommentReq.getContent();
        if(post_status.equals("inactive")) { //삭제된 게시글이면 댓글 등록 불가
            throw new BaseException(IMPOSSIBLE_POST);
        }
        else if(content.isEmpty()) { //빈 내용이면 등록 불가
            throw new BaseException(EMPTY_CONTENT);
        }
        else if(content.length() >= 100) { //내용 100자 이내 제한
            throw new BaseException(OVER_CONTENT);
        }
        try {
            PostCommentDto.RegisterCommentRes registerCommentRes = postCommentRepository.insertComment(registerCommentReq);
            return registerCommentRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 게시글 댓글 수정
    public void modifyComment(PostCommentDto.PatchCommentReq post) throws BaseException{
        int postCommentIdx = post.getPostCommentIdx();
        int userIdx = post.getUserIdx();
        int postIdx = post.getPostIdx();
        String content = post.getContent();
        int user = postCommentRepository.getUserIdxByPostCommentIdx(postCommentIdx);
        String status = postCommentRepository.getStatusByPostCommentIdx(postCommentIdx);
        String post_status = postCommentRepository.getStatusByPostIdx(postIdx);
        if(status.equals("inactive")) { //삭제된 댓글이면 수정 불가
            throw new BaseException(IMPOSSIBLE_POST_COMMENT);
        }
        else if(post_status.equals("inactive")) { //삭제된 게시글이면 수정 불가
            throw new BaseException(IMPOSSIBLE_POST);
        }
        else if(userIdx != user) { //댓글쓴 유저가 아니면 수정 불가
            throw new BaseException(USER_NOT_EQUAL_COMMENT);
        }
        else if(content.isEmpty()) { //빈 내용 수정 불가
            throw new BaseException(EMPTY_CONTENT);
        }
        else if(content.length() >= 100) { //내용 100자 이내 제한
            throw new BaseException(OVER_CONTENT);
        }
        else{
            int result = postCommentRepository.modifyComment(post);
            if(result == 0){ // 0이면 에러가 발생
                throw new BaseException(MODIFY_FAIL_POST_COMMENT);
            }
        }

        try {

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 게시글 댓글 삭제
    public void modifyCommentStatus(PostCommentDto.PatchDeleteReq post) throws BaseException{
        int postCommentIdx = post.getPostCommentIdx();
        int userIdx = post.getUserIdx();
        int user = postCommentRepository.getUserIdxByPostCommentIdx(postCommentIdx);
        String status = postCommentRepository.getStatusByPostCommentIdx(postCommentIdx);
        if(status.equals("inactive")) { //삭제된 댓글 삭제 불가
            throw new BaseException(IMPOSSIBLE_POST_COMMENT);
        }
        else if(userIdx != user) { //댓글 쓴 유저가 아니면 삭제 불가
            throw new BaseException(USER_NOT_EQUAL_COMMENT);
        }
        else {
            int result = postCommentRepository.modifyCommentStatus(post);
            if(result == 0){ // 0이면 에러가 발생
                throw new BaseException(DELETE_FAIL_POST_COMMENT);
            }
        }

        try {

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 게시글별 댓글 조회
    public List<PostCommentDto.GetCommentRes> getCommentsByPostIdx(int postIdx) throws BaseException {
        String status = postCommentRepository.getStatusByPostIdx(postIdx);
        if(status.equals("inactive")) { //삭제된 게시글은 댓글 조회 불가
            throw new BaseException(IMPOSSIBLE_POST); //게시글이 존재하지 않음
        }
        try {
            List<PostCommentDto.GetCommentRes> getCommentRes = postCommentRepository.getCommentsByPostIdx(postIdx);
            return getCommentRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 특정 유저의 댓글 조회
    public List<PostCommentDto.GetCommentRes> getCommentsByUserIdx(int userIdx) throws BaseException {
        //PostCommentsql에 삭제된 게시글 댓글 조회 불가능 하도록 처리함
        try {
            List<PostCommentDto.GetCommentRes> getCommentsRes = postCommentRepository.getCommentsByUserIdx(userIdx);
            return getCommentsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
