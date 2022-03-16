package com.bit.kodari.service;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.CommentLikeDto;
import com.bit.kodari.dto.ReportDto;
import com.bit.kodari.repository.post.PostRepository;
import com.bit.kodari.repository.report.ReportRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 토론장 댓글 좋아요 선택(POST)
    @Transactional
    public ReportDto.PostReportRes choosePostReport(ReportDto.RegisterPostReportReq registerPostReportReq, int respondent) throws BaseException {
        int userIdx = registerPostReportReq.getReporter();
        int postIdx = registerPostReportReq.getPostIdx();
        boolean exist_user = reportRepository.getExistUser(postIdx, userIdx);

        //유저가 존재하면 신고 불가
        if(exist_user) {
            throw new BaseException(ALREADY_REPORT);
        }
        try {
            return reportRepository.choosePostReport(registerPostReportReq, respondent);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
