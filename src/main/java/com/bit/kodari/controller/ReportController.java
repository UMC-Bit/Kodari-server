package com.bit.kodari.controller;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.ReportDto;
import com.bit.kodari.repository.report.ReportRepository;
import com.bit.kodari.service.ReportService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.bit.kodari.config.BaseResponseStatus.ALREADY_REPORT;
import static com.bit.kodari.config.BaseResponseStatus.INVALID_USER_JWT;

@Slf4j
@RestController
@RequestMapping("/reports")
public class ReportController {
    @Autowired
    ReportService reportService;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    private final JwtService jwtService;

    public ReportController(ReportService reportService, JwtService jwtService) {
        this.reportService = reportService;
        this.jwtService = jwtService;
    }


    //게시글 신고 기능
    @PostMapping(value="/post")
    @ApiOperation(value = "게시글 신고", notes = "토론장 게시글 신고함.")
    public BaseResponse<ReportDto.PostReportRes> checkPostReport(@RequestBody ReportDto.RegisterPostReportReq registerPostReportReq) throws BaseException {
        int postIdx = registerPostReportReq.getPostIdx();
        int reportCnt = reportRepository.getPostReportCnt(postIdx);
        int userIdx = registerPostReportReq.getReporter();
        boolean exist_user = reportRepository.getPostExistUser(postIdx, userIdx);

        //jwt에서 idx 추출.
        int userIdxByJwt = jwtService.getUserIdx();
        //신고하는 유저와 접근한 유저가 같은지 확인
        if(registerPostReportReq.getReporter() != userIdxByJwt){
            return new BaseResponse<>(INVALID_USER_JWT);
        }
        if(reportCnt < 2) { //신고 3회 초과 -> 추가
            //유저가 존재하면 신고 불가
            if(exist_user) {
                throw new BaseException(ALREADY_REPORT);
            }
            return createPostReport(registerPostReportReq);
        }
        else{ //신고 3회 초과 -> 게시글 삭제
            //유저가 존재하면 신고 불가
            if(exist_user) {
                throw new BaseException(ALREADY_REPORT);
            }
            ReportDto.DeletePost delete = new ReportDto.DeletePost(postIdx);
            return deletePost(delete);
        }
    }

    /*
    게시글 신고 횟수 증가
  */
    @PostMapping(value="/post/report")
    @ApiOperation(value = "게시글 신고", notes = "토론장 게시글 신고함.")
    public BaseResponse<ReportDto.PostReportRes> createPostReport(ReportDto.RegisterPostReportReq registerPostReportReq){
        int postIdx = registerPostReportReq.getPostIdx();
        int respondent = reportRepository.getRespondentByPostIdx(postIdx);
        try {
            ReportDto.PostReportRes registerPostReportRes = reportService.choosePostReport(registerPostReportReq, respondent);
            return new BaseResponse<>(registerPostReportRes, BaseResponseStatus.SUCCESS_POST_REPORT_REGISTER);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        게시글 신고 횟수 3회 -> 게시글 삭제
     */
    @PatchMapping("/post/delete")
    @ApiOperation(value = "게시글 삭제", notes = "신고 횟수 3회 초과된 게시글 삭제함.")
    public BaseResponse<ReportDto.PostReportRes> deletePost(@RequestBody ReportDto.DeletePost post){
        try {
            ReportDto.PostReportRes deletePostRes = reportService.deletePost(post);
            return new BaseResponse<>(deletePostRes, BaseResponseStatus.SUCCESS_POST_DELETE);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    //댓글 신고


    //댓글 신고 기능
    @PostMapping(value="/post/comment")
    @ApiOperation(value = "댓글 신고", notes = "토론장 댓글 신고함.")
    public BaseResponse<ReportDto.PostCommentReportRes> checkPostCommentReport(@RequestBody ReportDto.RegisterPostCommentReportReq registerPostCommentReportReq) throws BaseException {
        int postCommentIdx = registerPostCommentReportReq.getPostCommentIdx();
        int reportCnt = reportRepository.getPostCommentReportCnt(postCommentIdx);
        int userIdx = registerPostCommentReportReq.getReporter();
        boolean exist_user = reportRepository.getPostCommentExistUser(postCommentIdx, userIdx);
        //jwt에서 idx 추출.
        int userIdxByJwt = jwtService.getUserIdx();
        //신고하는 유저와 접근한 유저가 같은지 확인
        if(registerPostCommentReportReq.getReporter() != userIdxByJwt){
            return new BaseResponse<>(INVALID_USER_JWT);
        }


        if(reportCnt < 2) { //신고 3회 초과 -> 추가
            //유저가 존재하면 신고 불가
            if(exist_user) {
                throw new BaseException(ALREADY_REPORT);
            }
            return createPostCommentReport(registerPostCommentReportReq);
        }
        else{ //신고 3회 초과 -> 게시글 삭제
            //유저가 존재하면 신고 불가
            if(exist_user) {
                throw new BaseException(ALREADY_REPORT);
            }
            ReportDto.DeletePostComment delete = new ReportDto.DeletePostComment(postCommentIdx);
            return deletePostComment(delete);
        }
    }

    /*
        댓글 신고 횟수 증가
  */
    @PostMapping(value="/post/comment/report")
    @ApiOperation(value = "댓글 신고", notes = "토론장 댓글 신고함.")
    public BaseResponse<ReportDto.PostCommentReportRes> createPostCommentReport(ReportDto.RegisterPostCommentReportReq registerPostCommentReportReq){
        int postCommentIdx = registerPostCommentReportReq.getPostCommentIdx();
        int respondent = reportRepository.getRespondentByPostCommentIdx(postCommentIdx);
        try {
            ReportDto.PostCommentReportRes registerPostCommentReportRes = reportService.choosePostCommentReport(registerPostCommentReportReq, respondent);
            return new BaseResponse<>(registerPostCommentReportRes, BaseResponseStatus.SUCCESS_COMMENT_REPORT_REGISTER);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        댓글 신고 횟수 3회 -> 삭제 후, 댓글 내용 변경 "운영원칙에 위배된 댓글입니다."
     */
    @PatchMapping("/post/comment/delete")
    @ApiOperation(value = "댓글 삭제", notes = "신고 횟수 3회 초과된 댓글 삭제함.")
    public BaseResponse<ReportDto.PostCommentReportRes> deletePostComment(@RequestBody ReportDto.DeletePostComment comment){
        try {
            ReportDto.PostCommentReportRes deletePostCommentRes = reportService.deletePostComment(comment);
            return new BaseResponse<>(deletePostCommentRes, BaseResponseStatus.SUCCESS_COMMENT_DELETE);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    //답글 신고


    //답글 신고 기능
    @PostMapping(value="/post/reply")
    @ApiOperation(value = "답글 신고", notes = "토론장 답글 신고함.")
    public BaseResponse<ReportDto.PostReplyReportRes> checkPostReplyReport(@RequestBody ReportDto.RegisterPostReplyReportReq registerPostReplyReportReq) throws BaseException {
        int postReplyIdx = registerPostReplyReportReq.getPostReplyIdx();
        int reportCnt = reportRepository.getPostReplyReportCnt(postReplyIdx);
        int userIdx = registerPostReplyReportReq.getReporter();
        boolean exist_user = reportRepository.getPostReplyExistUser(postReplyIdx, userIdx);
        //jwt에서 idx 추출.
        int userIdxByJwt = jwtService.getUserIdx();
        //신고하는 유저와 접근한 유저가 같은지 확인
        if(registerPostReplyReportReq.getReporter() != userIdxByJwt){
            return new BaseResponse<>(INVALID_USER_JWT);
        }
        //유저가 존재하면 신고 불가
        if(exist_user) {
            throw new BaseException(ALREADY_REPORT);
        }
        if(reportCnt < 2) { //신고 3회 초과 -> 추가

            return createPostReplyReport(registerPostReplyReportReq);
        }
        else{ //신고 3회 초과 -> 게시글 삭제
            ReportDto.DeletePostReply delete = new ReportDto.DeletePostReply(postReplyIdx);
            return deletePostReply(delete);
        }
    }

    /*
    답글 신고 횟수 증가
  */
    @PostMapping(value="/post/reply/report")
    @ApiOperation(value = "답글 신고", notes = "토론장 답글 신고함.")
    public BaseResponse<ReportDto.PostReplyReportRes> createPostReplyReport(ReportDto.RegisterPostReplyReportReq registerPostReplyReportReq){
        int postReplyIdx = registerPostReplyReportReq.getPostReplyIdx();
        int respondent = reportRepository.getRespondentByPostReplyIdx(postReplyIdx);
        try {
            ReportDto.PostReplyReportRes registerPostReplyReportRes = reportService.choosePostReplyReport(registerPostReplyReportReq, respondent);
            return new BaseResponse<>(registerPostReplyReportRes, BaseResponseStatus.SUCCESS_REPLY_REPORT_REGISTER);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /*
        답글 신고 횟수 3회 -> 삭제 후, 답글 내용 변경 "운영원칙에 위배된 댓글입니다."
     */
    @PatchMapping("/post/reply/delete")
    @ApiOperation(value = "답글 삭제", notes = "신고 횟수 3회 초과된 답글 삭제함.")
    public BaseResponse<ReportDto.PostReplyReportRes> deletePostReply(@RequestBody ReportDto.DeletePostReply reply){
        try {
            ReportDto.PostReplyReportRes deletePostReplyRes = reportService.deletePostReply(reply);
            return new BaseResponse<>(deletePostReplyRes, BaseResponseStatus.SUCCESS_REPLY_DELETE);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }




}
