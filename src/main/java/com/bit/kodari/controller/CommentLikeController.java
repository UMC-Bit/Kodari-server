package com.bit.kodari.controller;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.CommentLikeDto;
import com.bit.kodari.repository.commentlike.CommentLikeRepository;
import com.bit.kodari.service.CommentLikeService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;

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
        토론장 댓글 좋아요 선택 체크 횟수 확인
      */
    @PostMapping(value="/choose")
    @ApiOperation(value = "좋아요 선택 체크", notes = "토론장 댓글 좋아요 등록, 취소 체크 함")
    public BaseResponse<CommentLikeDto.CommentLikeRes> checkCommentLike(@RequestBody CommentLikeDto.RegisterCommentLikeReq registerCommentLikeReq){
        int userIdx = registerCommentLikeReq.getUserIdx();
        int postCommentIdx = registerCommentLikeReq.getPostCommentIdx();
        String exist_user = commentLikeRepository.getUser(userIdx, postCommentIdx);
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //유저가 존재하면 좋아요 취소
            if(exist_user.equals("true")) {
                int commentLikeIdx = commentLikeRepository.getCommentLikeIdxByIdx(userIdx, postCommentIdx);
                CommentLikeDto.CommentLikeReq deleteLikeReq = new CommentLikeDto.CommentLikeReq(userIdx, commentLikeIdx);
                return deleteCommentLike(deleteLikeReq);
                //commentLikeService.checkUserLike(exist_user, commentLikeIdx, response);
            }else{ // 좋아요 등록
                return createCommentLike(registerCommentLikeReq);
            }
    }


    /*
        토론장 댓글 좋아요 선택
      */
    @PostMapping(value="/like")
    @ApiOperation(value = "좋아요 선택", notes = "토론장 댓글 좋아요를 선택함.")
    public BaseResponse<CommentLikeDto.CommentLikeRes> createCommentLike(@RequestBody CommentLikeDto.RegisterCommentLikeReq registerCommentLikeReq){
        int userIdx = registerCommentLikeReq.getUserIdx();
        int postCommentIdx = registerCommentLikeReq.getPostCommentIdx();
        int commentLikeIdx = commentLikeRepository.getCommentLikeIdxByIdx(userIdx, postCommentIdx);
        try {
            CommentLikeDto.CommentLikeRes registerCommentLikeRes = commentLikeService.chooseCommentLike(registerCommentLikeReq);
            return new BaseResponse<>(registerCommentLikeRes, BaseResponseStatus.SUCCESS_COMMENT_LIKE_REGISTER);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        토론장 댓글 좋아요 취소
      */
    @PostMapping(value="/delete")
    @ApiOperation(value = "좋아요 취소 선택", notes = "토론장 댓글 좋아요를 선택함.")
    public BaseResponse<CommentLikeDto.CommentLikeRes> deleteCommentLike(@RequestBody CommentLikeDto.CommentLikeReq deleteLikeReq){
        try {
            CommentLikeDto.CommentLikeRes deleteLikeRes = commentLikeService.deleteLike(deleteLikeReq);
            return new BaseResponse<>(deleteLikeRes, BaseResponseStatus.SUCCESS_COMMENT_LIKE_DELETE);
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
    /*
    @DeleteMapping("/delete")
    @ApiOperation(value = "좋아요 삭제", notes = "토론장 댓글의 좋아요 삭제함.")
    public BaseResponse<CommentLikeDto.DeleteLikeRes> DeleteCommentLike(@RequestParam int commentLikeIdx) {
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //같다면 유저네임 변경
            CommentLikeDto.DeleteLikeReq deleteLikeReq = new CommentLikeDto.DeleteLikeReq(commentLikeIdx);
            CommentLikeDto.DeleteLikeRes deleteLikeRes = commentLikeService.deleteLike(deleteLikeReq);
            //String result = "토론장 댓글의 좋아요가 삭제되었습니다.";
            return new BaseResponse<>(deleteLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }*/

}
