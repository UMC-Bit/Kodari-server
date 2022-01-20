package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.PostLikeDto;
import com.bit.kodari.repository.postlike.PostLikeRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class PostLikeService {
    @Autowired
    PostLikeRepository postLikeRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public PostLikeService(PostLikeRepository postLikeRepository, JwtService jwtService) {
        this.postLikeRepository = postLikeRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }


    // 토론장 게시글 좋아요/싫어요 선택(POST)
    public PostLikeDto.RegisterLikeRes chooseLike(PostLikeDto.RegisterLikeReq registerLikeReq) throws BaseException {
        int postIdx = registerLikeReq.getPostIdx();
        int userIdx = registerLikeReq.getUserIdx();
        String status = postLikeRepository.getStatusByPostIdx(postIdx);
        if(status.equals("inactive")) {
            throw new BaseException(IMPOSSIBLE_POST);
        }
        try {
            return postLikeRepository.chooseLike(registerLikeReq);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 게시글 좋아요/싫어요 수정
    public void modifyLike(PostLikeDto.PatchLikeReq post, PostLikeDto.DeleteLikeReq delete) throws BaseException {
        int postLikeIdx = post.getPostLikeIdx();
        int userIdx = post.getUserIdx();
        int likeType = post.getLikeType();
        int user = postLikeRepository.getUserIdxByPostLikeIdx(postLikeIdx);
        String status = postLikeRepository.getStatusByPostLikeIdx(postLikeIdx);
        int equal_likeType = postLikeRepository.getLikeTypeByPostLikeIdx(postLikeIdx);
        if(status.equals("inactive")) {
            throw new BaseException(IMPOSSIBLE_POST);
        }
        else if(userIdx != user) {
            throw new BaseException(USER_NOT_EQUAL_LIKE); //4073
        }
        else {
            if(likeType == equal_likeType){
                int result = postLikeRepository.deleteLike(delete);
                if(result == 0) {
                    throw new BaseException(DELETE_FAIL_POST_LIKE);
                }
            }
            else {
                int result = postLikeRepository.modifyLike(post);
                if (result == 0) { // 0이면 에러가 발생
                    throw new BaseException(MODIFY_FAIL_POST_LIKE); //4070
                }
            }
        }
        try {

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //토론장 게시글 좋아요/싫어요 삭제
    public void deleteLike(PostLikeDto.DeleteLikeReq post) throws BaseException {
        try {
            int result = postLikeRepository.deleteLike(post);
            if (result == 0) { // 0이면 에러가 발생
                throw new BaseException(DELETE_FAIL_POST_LIKE);
            }

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }



    // 특정 게시글별 좋아요 조회
    public List<PostLikeDto.GetLikeRes> getLikesByPostIdx(int postIdx) throws BaseException {
        String status = postLikeRepository.getStatusByPostIdx(postIdx);
        if(status.equals("inactive")) {
            throw new BaseException(IMPOSSIBLE_POST); // 게시글이 존재하지 않음.
        }
        try {
            List<PostLikeDto.GetLikeRes> getLikeRes = postLikeRepository.getLikesByPostIdx(postIdx);
            return getLikeRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 게시글별 싫어요 조회
    public List<PostLikeDto.GetDislikeRes> getDislikesByPostIdx(int postIdx) throws BaseException {
        String status = postLikeRepository.getStatusByPostIdx(postIdx);
        if(status.equals("inactive")) {
            throw new BaseException(IMPOSSIBLE_POST); // 게시글이 존재하지 않음.
        }
        try {
            List<PostLikeDto.GetDislikeRes> getDislikeRes = postLikeRepository.getDislikesByPostIdx(postIdx);
            return getDislikeRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }




}
