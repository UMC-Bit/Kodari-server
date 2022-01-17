package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.PostCommentDto;
import com.bit.kodari.repository.postcomment.PostCommentRepository;
import com.bit.kodari.service.PostCommentService;
import com.bit.kodari.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/comments")
public class PostCommentController {
    @Autowired
    PostCommentService postCommentService;
    @Autowired
    PostCommentRepository postCommentRepository;
    @Autowired
    private final JwtService jwtService;

    public PostCommentController(PostCommentService postCommentService, JwtService jwtService) {
        this.postCommentService = postCommentService;
        this.jwtService = jwtService;
    }

    /*
        토론장 게시글 댓글 작성
      */
    @PostMapping(value="/register")
    @ApiOperation(value = "게시글 댓글 등록", notes = "토론장 게시글 댓글 등록함.")
    public BaseResponse<PostCommentDto.RegisterCommentRes> createPostComment(@RequestBody PostCommentDto.RegisterCommentReq registerCommentReq){
        int userIdx = registerCommentReq.getUserIdx();
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PostCommentDto.RegisterCommentRes registerCommentRes = postCommentService.insertPostComment(registerCommentReq);
            return new BaseResponse<>(registerCommentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        토론장 게시글 댓글 수정
     */
    @PatchMapping("/update/{postCommentIdx}")
    @ApiOperation(value = "게시글 댓글 수정", notes = "토론장 게시글 댓글 수정함.")
    public BaseResponse<String> UpdateComment(@PathVariable("postCommentIdx") int postCommentIdx, @RequestBody PostCommentDto.PatchCommentReq post){
        int userIdx = postCommentRepository.getUserIdxByPostCommentIdx(postCommentIdx);
        int postIdx = postCommentRepository.getPostIdxByPostCommentIdx(postCommentIdx);
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PostCommentDto.PatchCommentReq patchCommentReq = new PostCommentDto.PatchCommentReq(postCommentIdx, userIdx, postIdx, post.getContent());
            postCommentService.modifyComment(patchCommentReq);
            String result = "댓글이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        토론장 게시글 댓글 삭제
     */
    @PatchMapping("/status/{postCommentIdx}")
    @ApiOperation(value = "게시글 댓글 삭제", notes = "토론장 게시글 댓글 삭제함.")
    public BaseResponse<String> modifyCommentStatus(@PathVariable("postCommentIdx") int postCommentIdx) {
        int userIdx = postCommentRepository.getUserIdxByPostCommentIdx(postCommentIdx);
        int postIdx = postCommentRepository.getPostIdxByPostCommentIdx(postCommentIdx);
        try {
            /*
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }*/
            //같다면 유저네임 변경
            PostCommentDto.PatchDeleteReq patchDeleteReq = new PostCommentDto.PatchDeleteReq(postCommentIdx, userIdx, postIdx);
            postCommentService.modifyCommentStatus(patchDeleteReq);
            String result = "댓글이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        토론장 게시글별 댓글 조회
     */
    @GetMapping("/post") // (GET) 127.0.0.1:9000/comments
    @ApiOperation(value = "토론장 게시글별 댓글 목록 조회", notes = "토론장 게시글 전체 조회함")
    public BaseResponse<List<PostCommentDto.GetCommentRes>> getComments(@RequestParam int postIdx) {
        try {
            List<PostCommentDto.GetCommentRes> getCommentsRes = postCommentService.getCommentsByPostIdx(postIdx);
            return new BaseResponse<>(getCommentsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //토론장 게시글 유저별 댓글 조회
    @GetMapping("/user") // (GET) 127.0.0.1:9000/comments
    @ApiOperation(value = "유저별 댓글 목록 조회", notes = "토론장 게시글 유저별 댓글을 조회함")
    public BaseResponse<List<PostCommentDto.GetCommentRes>> getPosts(@RequestParam int userIdx) {
        try {
            //유저별 댓글 조회
            List<PostCommentDto.GetCommentRes> getCommentsRes = postCommentService.getCommentsByUserIdx(userIdx);
            return new BaseResponse<>(getCommentsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
