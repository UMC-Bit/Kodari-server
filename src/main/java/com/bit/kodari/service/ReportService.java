package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.PostDto;
import com.bit.kodari.dto.ReportDto;
import com.bit.kodari.repository.report.ReportRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bit.kodari.config.BaseResponseStatus.*;

@Slf4j
@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public ReportService(ReportRepository reportRepository, JwtService jwtService) {
        this.reportRepository = reportRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // 토론장 게시글 신고 선택(POST)
    @Transactional
    public ReportDto.PostReportRes choosePostReport(ReportDto.RegisterPostReportReq registerPostReportReq, int respondent) throws BaseException {
        int userIdx = registerPostReportReq.getReporter();
        if(userIdx == respondent) {
            throw new BaseException(IMPOSSIBLE_POST_REPORT); //자신의 게시글을 신고불가
        }
        try {
            return reportRepository.choosePostReport(registerPostReportReq, respondent);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 게시글 삭제
    @Transactional
    public ReportDto.PostReportRes deletePost(ReportDto.DeletePost delete) throws BaseException {
        int postIdx = delete.getPostIdx();
        List<ReportDto.GetCommentDeleteRes> getCommentDeleteRes = reportRepository.getPostCommentIdxByPostIdx(postIdx);
        List<ReportDto.GetCommentLikeDeleteRes> getCommentLikeDeleteRes = reportRepository.getCommentLikeIdxByPostIdx(postIdx);
        List<ReportDto.GetLikeDeleteRes> getLikeDeleteRes = reportRepository.getPostLikeIdxByPostIdx(postIdx);
        List<ReportDto.GetReplyDeleteRes> getReplyDeleteRes = reportRepository.getReplyIdxByPostIdx(postIdx);
        int userIdx = reportRepository.getRespondentByPostIdx(postIdx);
        try {
            ReportDto.PostReportRes deleteRes = reportRepository.deletePost(delete);
            if (deleteRes.getUserIdx() == 0) {
                throw new BaseException(DELETE_FAIL_POST);
            }
            //토론장 게시글 삭제 시 유저 report + 1
            ReportDto.UserReportRes userReportRes = reportRepository.updateUserReport(userIdx);
            if(userReportRes.getUserIdx() == 0) {
                throw new BaseException(FAIL_REPORT_ADD);
            }
            //게시글 삭제되면 관련된 댓글, 답글, 좋아요/싫어요 삭제
            for(int i=0; i< getCommentDeleteRes.size(); i++){
                int resultComment = reportRepository.modifyCommentStatus(getCommentDeleteRes.get(i).getPostCommentIdx());
                if(resultComment == 0) {
                    throw new BaseException(DELETE_FAIL_POST_COMMENT);
                }
            }
            for(int i=0; i< getCommentLikeDeleteRes.size(); i++){
                int resultCommentLike = reportRepository.deleteCommentLikeStatus(getCommentLikeDeleteRes.get(i).getCommentLikeIdx());
                if(resultCommentLike == 0) {
                    throw new BaseException(DELETE_FAIL_COMMENT_LIKE);
                }
            }
            for(int i=0; i< getLikeDeleteRes.size(); i++){
                int resultLike = reportRepository.deleteLikeStatus(getLikeDeleteRes.get(i).getPostLikeIdx());
                if(resultLike == 0) {
                    throw new BaseException(DELETE_FAIL_POST_LIKE);
                }
            }
            for(int i=0; i< getReplyDeleteRes.size(); i++){
                int resultReply = reportRepository.modifyReplyStatus(getReplyDeleteRes.get(i).getPostReplyIdx());
                if(resultReply == 0) {
                    throw new BaseException(DELETE_FAIL_COMMENT_REPLY);
                }
            }

            return deleteRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }



    //댓글 신고



    // 토론장 댓글 신고 선택(POST)
    @Transactional
    public ReportDto.PostCommentReportRes choosePostCommentReport(ReportDto.RegisterPostCommentReportReq registerPostCommentReportReq, int respondent) throws BaseException {
        int userIdx = registerPostCommentReportReq.getReporter();
        if(userIdx == respondent) {
            throw new BaseException(IMPOSSIBLE_POST_REPORT); //자신의 게시글을 신고불가
        }
        try {
            return reportRepository.choosePostCommentReport(registerPostCommentReportReq, respondent);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 댓글 삭제
    @Transactional
    public ReportDto.PostCommentReportRes deletePostComment(ReportDto.DeletePostComment delete) throws BaseException {
        int userIdx = reportRepository.getRespondentByPostCommentIdx(delete.getPostCommentIdx());
        try {
            ReportDto.PostCommentReportRes deleteRes = reportRepository.deletePostComment(delete);
            if (deleteRes.getUserIdx() == 0) {
                throw new BaseException(DELETE_FAIL_POST_COMMENT);
            }
            //토론장 게시글 삭제 시 유저 report + 1
            ReportDto.UserReportRes userReportRes = reportRepository.updateUserReport(userIdx);
            if(userReportRes.getUserIdx() == 0) {
                throw new BaseException(FAIL_REPORT_ADD);
            }
            return deleteRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //토론장 답글 신고

    // 토론장 답글 신고 선택(POST)
    @Transactional
    public ReportDto.PostReplyReportRes choosePostReplyReport(ReportDto.RegisterPostReplyReportReq registerPostReplyReportReq, int respondent) throws BaseException {
        int userIdx = registerPostReplyReportReq.getReporter();
        if(userIdx == respondent) {
            throw new BaseException(IMPOSSIBLE_POST_REPORT); //자신의 게시글을 신고불가
        }
        try {
            return reportRepository.choosePostReplyReport(registerPostReplyReportReq, respondent);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //토론장 답글 삭제
    @Transactional
    public ReportDto.PostReplyReportRes deletePostReply(ReportDto.DeletePostReply delete) throws BaseException {
        int userIdx = reportRepository.getRespondentByPostReplyIdx(delete.getPostReplyIdx()); //신고당하는 유저
        try {
            ReportDto.PostReplyReportRes deleteReplyRes = reportRepository.deletePostReply(delete);
            if (deleteReplyRes.getUserIdx() == 0) {
                throw new BaseException(DELETE_FAIL_COMMENT_REPLY);
            }
            //토론장 게시글 삭제 시 유저 report + 1
            ReportDto.UserReportRes userReportRes = reportRepository.updateUserReport(userIdx);
            if(userReportRes.getUserIdx() == 0) {
                throw new BaseException(FAIL_REPORT_ADD);
            }
            return deleteReplyRes;
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
