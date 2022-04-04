package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.PostDto;
import com.bit.kodari.repository.post.PostRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;
import static com.bit.kodari.dto.PostDto.*;

@Slf4j
@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public PostService(PostRepository postRepository, JwtService jwtService) {
        this.postRepository = postRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }


    // 토론장 게시글 등록(POST)
    @Transactional
    public RegisterRes insertPost(RegisterReq registerReq) throws BaseException {
        String content = registerReq.getContent();
        String tmp_content = content.replaceAll(" ", "");
        int reportCnt = postRepository.getUserReport(registerReq.getUserIdx());
        if(content.isEmpty() || tmp_content.isEmpty()) { //게시글 내용이 없는 경우 validation 처리
            throw new BaseException(EMPTY_CONTENT);
        }
        else if(content.length() >= 500) { //게시글 500자 이내 제한
            throw new BaseException(OVER_CONTENT);
        }
        else if(reportCnt > 2) { //신고 당한 횟수가 3회 초과 시 토론장 접근 제한
            throw new BaseException(BLOCKED_USER);
        }
        try {
            return postRepository.insert(registerReq);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 게시글 수정
    @Transactional
    public void modifyPost(PatchPostReq post) throws BaseException{
        int postIdx = post.getPostIdx();
        String content = post.getContent();
        int user = postRepository.getUserIdxByPostIdx(postIdx);
        int reportCnt = postRepository.getUserReport(user);
        String status = postRepository.getStatusByPostIdx(postIdx);
        String tmp_content = content.replaceAll(" ", "");
        if(status.equals("inactive")) { //삭제된 글은 수정 불가
            throw new BaseException(IMPOSSIBLE_POST);
        }
        else if(content.isEmpty() || tmp_content.isEmpty()) { //게시글 내용 입력 없을 경우 validation 처리
            throw new BaseException(EMPTY_CONTENT);
        }
        //게시글 내용 0자 이하 제한
        else if(content.length() >= 500) { //게시글 500자 이내 제한
            throw new BaseException(OVER_CONTENT);
        }
        else if(reportCnt > 2) { //신고 당한 횟수가 3회 초과 시 토론장 접근 제한
            throw new BaseException(BLOCKED_USER);
        }
        else{
            int result = postRepository.modifyPost(post);
            if(result == 0){ // 0이면 에러가 발생
                throw new BaseException(MODIFY_FAIL_POST);
            }
        }

        try {

        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 게시글 삭제
    @Transactional
    public void modifyPostStatus(PatchDeleteReq post) throws BaseException{
        int postIdx = post.getPostIdx();
        List<PostDto.GetCommentDeleteRes> getCommentDeleteRes = postRepository.getPostCommentIdxByPostIdx(postIdx);
        List<PostDto.GetCommentLikeDeleteRes> getCommentLikeDeleteRes = postRepository.getCommentLikeIdxByPostIdx(postIdx);
        List<PostDto.GetLikeDeleteRes> getLikeDeleteRes = postRepository.getPostLikeIdxByPostIdx(postIdx);
        List<PostDto.GetReplyDeleteRes> getReplyDeleteRes = postRepository.getReplyIdxByPostIdx(postIdx);

        try{
            int result = postRepository.modifyPostStatus(post);
            if(result == 0){ // 0이면 에러가 발생
                throw new BaseException(DELETE_FAIL_POST); //407
            }
            //게시글 삭제되면 관련된 댓글, 답글, 좋아요/싫어요 삭제
            for(int i=0; i< getCommentDeleteRes.size(); i++){
                int resultComment = postRepository.modifyCommentStatus(getCommentDeleteRes.get(i).getPostCommentIdx());
                if(resultComment == 0) {
                    throw new BaseException(DELETE_FAIL_POST_COMMENT);
                }
            }
            for(int i=0; i< getCommentLikeDeleteRes.size(); i++){
                int resultCommentLike = postRepository.deleteCommentLikeStatus(getCommentLikeDeleteRes.get(i).getCommentLikeIdx());
                if(resultCommentLike == 0) {
                    throw new BaseException(DELETE_FAIL_COMMENT_LIKE);
                }
            }
            for(int i=0; i< getLikeDeleteRes.size(); i++){
                int resultLike = postRepository.deleteLikeStatus(getLikeDeleteRes.get(i).getPostLikeIdx());
                if(resultLike == 0) {
                    throw new BaseException(DELETE_FAIL_POST_LIKE);
                }
            }
            for(int i=0; i< getReplyDeleteRes.size(); i++){
                int resultReply = postRepository.modifyReplyStatus(getReplyDeleteRes.get(i).getPostReplyIdx());
                if(resultReply == 0) {
                    throw new BaseException(DELETE_FAIL_COMMENT_REPLY);
                }
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 토론장 게시글 목록 조회
    @Transactional
    public List<GetPostRes> getPosts() throws BaseException {
        int userIdx = jwtService.getUserIdx();
        List<PostDto.GetPostRes> postList = new ArrayList<PostDto.GetPostRes>();
        List<PostDto.GetPostRes> getPost = postRepository.getPosts();
        List<Integer> getBlock = postRepository.getBlockPosts(userIdx);
        try {
            for(int i=0; i < getPost.size(); i++) {
                if(!getBlock.contains(getPost.get(i).getUserIdx())){
                    postList.add(getPost.get(i));
                }
            }
            return postList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 유저의 게시글 조회
    @Transactional
    public List<GetPostRes> getPostsByUserIdx(int userIdx) throws BaseException {
        try {
            List<GetPostRes> getPostsRes = postRepository.getPostsByUserIdx(userIdx);
            return getPostsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 코인의 게시글 조회
    @Transactional
    public List<GetPostRes> getPostsByCoinName(String coinName) throws BaseException {
        int userIdx = jwtService.getUserIdx();
        List<PostDto.GetPostRes> postList = new ArrayList<PostDto.GetPostRes>();
        List<PostDto.GetPostRes> getPost = postRepository.getPostsByCoinName(coinName);
        List<Integer> getBlock = postRepository.getBlockPosts(userIdx);
        try {
            for(int i=0; i < getPost.size(); i++) {
                if(!getBlock.contains(getPost.get(i).getUserIdx())){
                    postList.add(getPost.get(i));
                }
            }
            return postList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 게시글 조회
    @Transactional
    public GetUserPostRes getPostsByPostIdx(int postIdx) throws BaseException {
        String status = postRepository.getStatusByPostIdx(postIdx);
        if (status.equals("inactive")) { //삭제된 게시글이면 조회 불가
            throw new BaseException(IMPOSSIBLE_POST);
        }

        try {
            PostDto.GetUserPostRes getUserPostRes = postRepository.getPostsByPostIdx(postIdx);
            return getUserPostRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
