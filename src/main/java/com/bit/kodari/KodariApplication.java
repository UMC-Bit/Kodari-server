package com.bit.kodari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 일정 시간마다 자동으로 메소드 호출하는 스케줄러 사용가능하게 한다.
public class KodariApplication {

    public static void main(String[] args) {
        SpringApplication.run(KodariApplication.class, args);

        // 메모리 사용량 출력
        long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("HEAP Size(M) : "+ heapSize / (1024*1024) + " MB");
    }

}
