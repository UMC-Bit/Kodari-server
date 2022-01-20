package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.dto.CommentLikeDto;
import com.bit.kodari.repository.commentlike.CommentLikeRepository;
import com.bit.kodari.service.CommentLikeService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comment/likes")
public class CommentLikeController {
    @Autowired
    CommentLikeService commentLikeService;
    @Autowired
    CommentLikeRepository commentLikeRepository;
    @Autowired
    private final JwtService jwtService;

    public CommentLikeController(CommentLikeService commentLikeService, JwtService jwtService) {
        this.commentLikeService = commentLikeService;
        this.jwtService = jwtService;
    }

    /*
        토론장 댓글 좋아요 선택
      */
    @PostMapping(value="/choose")
    @ApiOperation(value = "좋아요 선택", notes = "토론장 댓글 좋아요를 선택함.")
    public BaseResponse<CommentLikeDto.RegisterLikeRes> createCommentLike(@RequestBody CommentLikeDto.RegisterLikeReq registerLikeReq){
        int userIdx = registerLikeReq.getUserIdx();
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            CommentLikeDto.RegisterLikeRes registerLikeRes = commentLikeService.chooseCommentLike(registerLikeReq);
            return new BaseResponse<>(registerLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    /*
//            토론장 댓글 좋아요 수정
//         */
//    @PatchMapping("/update/{commentLikeIdx}")
//    @ApiOperation(value = "좋아요 수정", notes = "토론장 댓글의 좋아요 수정함.")
//    public BaseResponse<String> UpdateCommentLike(@PathVariable("commentLikeIdx") int commentLikeIdx, @RequestBody CommentLikeDto.PatchLikeReq post){
//        int userIdx = commentLikeRepository.getUserIdxByCommentLikeIdx(commentLikeIdx);
//        int postCommentIdx = commentLikeRepository.getPostCommentIdxByCommentLikeIdx(commentLikeIdx);
//        try {
////            //jwt에서 idx 추출.
////            int userIdxByJwt = jwtService.getUserIdx();
////            //userIdx와 접근한 유저가 같은지 확인
////            if(userIdx != userIdxByJwt){
////                return new BaseResponse<>(INVALID_USER_JWT);
////            }
//            //같다면 유저네임 변경
//            CommentLikeDto.PatchLikeReq patchLikeReq = new CommentLikeDto.PatchLikeReq(commentLikeIdx, userIdx, postCommentIdx, post.getLike());
//            CommentLikeDto.DeleteLikeReq deleteLikeReq = new CommentLikeDto.DeleteLikeReq(commentLikeIdx);
//            commentLikeService.modifyLike(patchLikeReq, deleteLikeReq);
//            String result = "토론장 댓글의 좋아요가 수정되었습니다.";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /*
            토론장 댓글 좋아요 삭제
         */
    @DeleteMapping("/delete")
    @ApiOperation(value = "좋아요 삭제", notes = "토론장 댓글의 좋아요 삭제함.")
    public BaseResponse<String> DeleteCommentLike(@RequestParam int commentLikeIdx, @RequestParam int like){
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            CommentLikeDto.DeleteLikeReq deleteLikeReq = new CommentLikeDto.DeleteLikeReq(commentLikeIdx, like);
            commentLikeService.deleteLike(deleteLikeReq);
            String result = "토론장 댓글의 좋아요가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
