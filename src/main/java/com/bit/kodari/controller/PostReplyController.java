package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.PostReplyDto;
import com.bit.kodari.repository.postreply.PostReplyRepository;
import com.bit.kodari.service.PostReplyService;
import com.bit.kodari.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reply")
public class PostReplyController {

    @Autowired
    PostReplyService postReplyService;
    @Autowired
    PostReplyRepository postReplyRepository;
    @Autowired
    private final JwtService jwtService;

    public PostReplyController(PostReplyService postReplyService, JwtService jwtService) {
        this.postReplyService = postReplyService;
        this.jwtService = jwtService;
    }



    /*
            토론장 게시글 댓글의 답글 작성
          */
    @PostMapping(value="/register")
    @ApiOperation(value = "게시글 답글 등록", notes = "토론장 게시글 댓글의 답글 등록함.")
    public BaseResponse<PostReplyDto.RegisterReplyRes> createReply(@RequestBody PostReplyDto.RegisterReplyReq registerReplyReq){
        int userIdx = registerReplyReq.getUserIdx();
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PostReplyDto.RegisterReplyRes registerReplyRes = postReplyService.insertCommentReply(registerReplyReq);
            return new BaseResponse<>(registerReplyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
            토론장 게시글 답글 수정
         */
    @PatchMapping("/update/{postReplyIdx}")
    @ApiOperation(value = "게시글 답글 수정", notes = "토론장 게시글 댓글의 답글 수정함.")
    public BaseResponse<String> UpdateReply(@PathVariable("postReplyIdx") int postReplyIdx, @RequestBody PostReplyDto.PatchReplyReq post){
        int userIdx = postReplyRepository.getUserIdxByPostReplyIdx(postReplyIdx);
        int postCommentIdx = postReplyRepository.getPostCommentIdxByPostReplyIdx(postReplyIdx);
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PostReplyDto.PatchReplyReq patchReplyReq = new PostReplyDto.PatchReplyReq(postReplyIdx, userIdx, postCommentIdx, post.getContent());
            postReplyService.modifyReply(patchReplyReq);
            String result = "답글이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
           토론장 게시글 답글 삭제
        */
    @PatchMapping("/status/{postReplyIdx}")
    @ApiOperation(value = "게시글 답글 삭제", notes = "토론장 게시글 댓글의 답글 삭제함.")
    public BaseResponse<String> modifyReplyStatus(@PathVariable("postReplyIdx") int postReplyIdx) {
        int userIdx = postReplyRepository.getUserIdxByPostReplyIdx(postReplyIdx);
        int postCommentIdx = postReplyRepository.getPostCommentIdxByPostReplyIdx(postReplyIdx);
        try {
            /*
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }*/
            //같다면 유저네임 변경
            PostReplyDto.PatchReplyDeleteReq patchReplyDeleteReq = new PostReplyDto.PatchReplyDeleteReq(postReplyIdx, userIdx, postCommentIdx);
            postReplyService.modifyReplyStatus(patchReplyDeleteReq);
            String result = "답글이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
           토론장 댓글별 답글 조회
        */
    @GetMapping("/comment") // (GET) 127.0.0.1:9000/replys
    @ApiOperation(value = "댓글별 답글 목록 조회", notes = "토론장 게시글 댓글별 답글 조회함")
    public BaseResponse<List<PostReplyDto.GetReplyRes>> getReplyComment(@RequestParam int postCommentIdx) {
        try {
            List<PostReplyDto.GetReplyRes> getReplysRes = postReplyService.getReplyByPostCommentIdx(postCommentIdx);
            return new BaseResponse<>(getReplysRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //토론장 게시글 유저별 답글 조회
    @GetMapping("/user") // (GET) 127.0.0.1:9000/replys
    @ApiOperation(value = "유저별 답글 목록 조회", notes = "토론장 게시글 댓글쓴 유저별 답글을 조회함")
    public BaseResponse<List<PostReplyDto.GetReplyRes>> getReplyUser(@RequestParam int userIdx) {
        try {
            //유저별 답글 조회
            List<PostReplyDto.GetReplyRes> getReplysRes = postReplyService.getReplyByUserIdx(userIdx);
            return new BaseResponse<>(getReplysRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        댓글별 답글 수 조회
     */
    @GetMapping("/count") // (GET) 127.0.0.1:9000/reply
    @ApiOperation(value = "답글 수 조회", notes = "댓별 답글 수 조회함")
    public BaseResponse<List<PostReplyDto.GetReplyCntRes>> getReplyCnt(@RequestParam int postCommentIdx) {
        try {
            List<PostReplyDto.GetReplyCntRes> getReplyCntRes = postReplyService.getReplyCntByPostCommentIdx(postCommentIdx);
            return new BaseResponse<>(getReplyCntRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
