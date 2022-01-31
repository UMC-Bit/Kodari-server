package com.bit.kodari.utils;

import com.bit.kodari.config.BaseException;
import com.bit.kodari.dto.ProfitDto;
import com.bit.kodari.service.ProfitService;
import com.bit.kodari.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class Scheduler {// 일정시간마다 작동 클래스
    private final TradeService tradeService;
    private final ProfitService profitService;


    @Autowired
    public Scheduler(TradeService tradeService, ProfitService profitService){
        this.tradeService = tradeService;
        this.profitService = profitService;

    }


    //"0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30, 10:00 and 10:30 every day.
    @Scheduled(cron = "0 0 0 * * *") // 초(0-59)   분(0-59)　　시간(0-23)　　일(1-31)　　월(1-12)　　요일(0-7) 로 스케줄 실행 시각 설정
    public void updateTradeByScheduler() throws BaseException , IOException {
//        System.out.println("스케줄러 테스트");
//        System.out.println(new Date().toString());

        // Profit에 있는 accountIdx 전체 조회
        List<Integer> getAllAccountIdxRes = profitService.getAllAccountIdx();
        // 모든 계좌의 수익내역 생성
        // 반복문으로 accountIdx 탐색하며 각각 수익내역 생성
        ProfitDto.PostProfitReq postProfitReq = new ProfitDto.PostProfitReq();
        for(int i=0;i<getAllAccountIdxRes.size();i++){
            System.out.println(getAllAccountIdxRes.get(i).intValue());
            postProfitReq.setAccountIdx(getAllAccountIdxRes.get(i).intValue());
            profitService.createProfit(postProfitReq);
        }

    }
}
