package com.bit.kodari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@EnableScheduling // 일정 시간마다 자동으로 메소드 호출하는 스케줄러 사용가능하게 한다.
public class KodariApplication {

    public static void main(String[] args) throws ParseException {
        SpringApplication.run(KodariApplication.class, args);

        // 메모리 사용량 출력
        long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("HEAP Size(M) : "+ heapSize / (1024*1024) + " MB");


        String now = "2022-01-30 01:00:00";
        String encodedDate = now.substring(0,10);
        System.out.println(encodedDate);
        encodedDate+="%20"; // 공백을 url로 인코딩
        encodedDate+= now.substring(11,13);
        encodedDate+= "%3A";
        encodedDate+= now.substring(14,16);
        encodedDate+= "%3A";
        encodedDate+= now.substring(17);
        System.out.println(encodedDate);

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date to = transFormat.parse(now);
        Date date = new Date();
        System.out.println(date.getTime()- to.getTime());
        long diffDay = (date.getTime()- to.getTime()) / (24*60*60*1000);
        System.out.println(diffDay);

    }

}
