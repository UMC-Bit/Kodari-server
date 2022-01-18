package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.PostLikeDto;
import com.bit.kodari.repository.postlike.PostLikeRepository;
import com.bit.kodari.service.PostLikeService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/likes")
public class PostLikeController {
    @Autowired
    PostLikeService postLikeService;
    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    private final JwtService jwtService;

    public PostLikeController(PostLikeService postLikeService, JwtService jwtService) {
        this.postLikeService = postLikeService;
        this.jwtService = jwtService;
    }

    /*
        토론장 게시글 좋아요/싫어요 선택
      */
    @PostMapping(value="/choose")
    @ApiOperation(value = "좋아요/싫어요 선택", notes = "토론장 게시글 좋아요/싫어요를 선택함.")
    public BaseResponse<PostLikeDto.RegisterLikeRes> createLike(@RequestBody PostLikeDto.RegisterLikeReq registerLikeReq){
        int userIdx = registerLikeReq.getUserIdx();
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PostLikeDto.RegisterLikeRes registerLikeRes = postLikeService.chooseLike(registerLikeReq);
            return new BaseResponse<>(registerLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        토론장 게시글 좋아요/싫어요 수정
     */
    @PatchMapping("/update/{postLikeIdx}")
    @ApiOperation(value = "좋아요/싫어요 수정", notes = "토론장 게시글의 좋아요/싫어요 수정함.")
    public BaseResponse<String> UpdatePostLike(@PathVariable("postLikeIdx") int postLikeIdx, @RequestBody PostLikeDto.PatchLikeReq post){
        int userIdx = postLikeRepository.getUserIdxByPostLikeIdx(postLikeIdx);
        int postIdx = postLikeRepository.getPostIdxByPostLikeIdx(postLikeIdx);
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PostLikeDto.PatchLikeReq patchLikeReq = new PostLikeDto.PatchLikeReq(postLikeIdx, userIdx, postIdx, post.getLikeType());
            postLikeService.modifyLike(patchLikeReq);
            String result = "토론장 게시글의 찬성/반대가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        토론장 게시글 좋아요/싫어요 삭제
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "좋아요/싫어요 삭제", notes = "토론장 게시글의 좋아요/싫어요 삭제함.")
    public BaseResponse<String> DeletePostLike(@RequestParam int postLikeIdx){
        int postIdx = postLikeRepository.getPostIdxByPostLikeIdx(postLikeIdx);
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            PostLikeDto.DeleteLikeReq deleteLikeReq = new PostLikeDto.DeleteLikeReq(postLikeIdx, postIdx);
            postLikeService.deleteLike(deleteLikeReq);
            String result = "토론장 게시글의 좋아요/싫어요가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        게시글별 좋아요 조회
     */
    @GetMapping("/like") // (GET) 127.0.0.1:9000/likes
    @ApiOperation(value = "좋아요 목록 조회", notes = "게시글별 좋아요 목록 조회함")
    public BaseResponse<List<PostLikeDto.GetLikeRes>> getLikes(@RequestParam int postIdx) {
        try {
            List<PostLikeDto.GetLikeRes> getLikeRes = postLikeService.getLikesByPostIdx(postIdx);
            return new BaseResponse<>(getLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*
        게시글별 싫어요 조회
     */
    @GetMapping("/dislike") // (GET) 127.0.0.1:9000/likes
    @ApiOperation(value = "싫어요 목록 조회", notes = "게시글별 싫어요 목록 조회함")
    public BaseResponse<List<PostLikeDto.GetDislikeRes>> getDislikes(@RequestParam int postIdx) {
        try {
            List<PostLikeDto.GetDislikeRes> getDislikeRes = postLikeService.getDislikesByPostIdx(postIdx);
            return new BaseResponse<>(getDislikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }






}
