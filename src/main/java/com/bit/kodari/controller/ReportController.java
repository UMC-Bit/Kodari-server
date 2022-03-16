package com.bit.kodari.controller;


import com.bit.kodari.config.BaseException;
import com.bit.kodari.config.BaseResponse;
import com.bit.kodari.config.BaseResponseStatus;
import com.bit.kodari.dto.CommentLikeDto;
import com.bit.kodari.dto.ReportDto;
import com.bit.kodari.repository.report.ReportRepository;
import com.bit.kodari.service.ReportService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<ReportDto.PostReportRes> checkPostReport(@RequestBody ReportDto.RegisterPostReportReq registerPostReportReq){
        int postIdx = registerPostReportReq.getPostIdx();
        int reportCnt = reportRepository.getPostReportCnt(postIdx);
        if(reportCnt <= 3) { //신고 3회 초과 -> 추가
            return createPostReport(registerPostReportReq);
        }
        else{ //신고 3회 초과 -> 게시글 삭제
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
    @ApiOperation(value = "게시글 수정", notes = "토론장 게시글 수정함.")
    public BaseResponse<ReportDto.PostReportRes> deletePost(@RequestBody ReportDto.DeletePost post){
        try {
            ReportDto.PostReportRes deletePostRes = reportService.deletePost(post);
            return new BaseResponse<>(deletePostRes, BaseResponseStatus.SUCCESS_POST_DELETE);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }







}
