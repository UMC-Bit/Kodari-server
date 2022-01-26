package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.PostCommentDto;
import com.bit.kodari.dto.PostDto;
import com.bit.kodari.repository.post.PostRepository;
import com.bit.kodari.service.PostService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Log;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.service.ResponseMessage;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;


@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    private final JwtService jwtService;

    public PostController(PostService postService, JwtService jwtService) {
        this.postService = postService;
        this.jwtService = jwtService;
    }


    /*
        토론장 게시글 작성
      */
    @PostMapping(value="/register")
    @ApiOperation(value = "게시글 등록", notes = "토론장 게시글 등록함.")
    public BaseResponse<PostDto.RegisterRes> createPost(@RequestBody PostDto.RegisterReq registerReq){
        int userIdx = registerReq.getUserIdx();
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
            PostDto.RegisterRes registerRes = postService.insertPost(registerReq);
            return new BaseResponse<>(registerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        토론장 게시글 수정
     */
    @PatchMapping("/update/{postIdx}")
    @ApiOperation(value = "게시글 수정", notes = "토론장 게시글 수정함.")
    public BaseResponse<String> UpdatePost(@PathVariable("postIdx") int postIdx, @RequestBody PostDto.PatchPostReq post){
        int userIdx = postRepository.getUserIdxByPostIdx(postIdx);
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            PostDto.PatchPostReq patchPostReq = new PostDto.PatchPostReq(postIdx, userIdx, post.getContent());
            postService.modifyPost(patchPostReq);
            String result = "토론장 게시글이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /*
        토론장 게시글 삭제
     */
    @PatchMapping("/status/{postIdx}")
    @ApiOperation(value = "토론장 게시글 삭제", notes = "토론장 게시글 삭제함.")
    public BaseResponse<String> modifyPostStatus(@PathVariable("postIdx") int postIdx) {
        int userIdx = postRepository.getUserIdxByPostIdx(postIdx);
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PostDto.PatchDeleteReq patchDeleteReq = new PostDto.PatchDeleteReq(postIdx);
            postService.modifyPostStatus(patchDeleteReq);
            String result = "토론장 게시글이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



    /*
        토론장 게시글 조회
     */
    @GetMapping("") // (GET) 127.0.0.1:9000/posts
    @ApiOperation(value = "토론장 게시글 목록 조회", notes = "토론장 게시글 전체 조회함")
    public BaseResponse<List<PostDto.GetPostRes>> getPosts(@RequestParam(required = false) Integer userIdx) {
        try {
            if (userIdx == null) {
                List<PostDto.GetPostRes> getPostsRes = postService.getPosts();
                return new BaseResponse<>(getPostsRes);
            }
            // query string인 userIdx이 있을 경우, 조건을 만족하는 상품정보들을 불러온다.
            List<PostDto.GetPostRes> getPostsRes = postService.getPostsByUserIdx(userIdx);
            return new BaseResponse<>(getPostsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        토론장 게시글별 조회
     */
    @GetMapping("/post") // (GET) 127.0.0.1:9000/posts
    @ApiOperation(value = "게시글별 조회", notes = "토론장 게시글별 조회함")
    public BaseResponse<PostDto.GetUserPostRes> getComments(@RequestParam int postIdx) {
        int userIdx = postRepository.getUserIdxByPostIdx(postIdx);
        List<PostDto.GetCommentDeleteRes> postCommentIdx = postRepository.getPostCommentIdxByPostIdx(postIdx);
        List<PostDto.GetReplyDeleteRes> postReplyIdx = postRepository.getReplyIdxByPostIdx(postIdx);
        try {

//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            // jwt validation check
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//
            PostDto.GetUserPostRes getUserPostRes = postService.getPostsByPostIdx(postIdx);
//            if(userIdx == userIdxByJwt){
//                getUserPostRes.setCheckWriter(true);
//            }
//            for(int i=0; i < postCommentIdx.size(); i++ ) {
//                List<PostDto.GetUserIdxRes> comment_userIdx = postRepository.getUserIdxByPostCommentIdx(postCommentIdx.get(i).getPostCommentIdx());
//                if(comment_userIdx.get(i).getUserIdx() == userIdxByJwt){
//                    getUserPostRes.setCheckCommentWriter(true);
//                }
//            }
//            for(int i=0; i < postReplyIdx.size(); i++ ) {
//                List<PostDto.GetUserIdxRes> reply_userIdx = postRepository.getUserIdxByPostReplyIdx(postReplyIdx.get(i).getPostReplyIdx());
//                if(reply_userIdx.get(i).getUserIdx() == userIdxByJwt){
//                    getUserPostRes.setCheckReplyWriter(true);
//                }
//            }
            return new BaseResponse<>(getUserPostRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
