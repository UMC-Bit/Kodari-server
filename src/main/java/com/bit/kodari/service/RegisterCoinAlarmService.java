package com.bit.kodari.service;


import com.bit.kodari.repository.registerCoinAlarm.RegisterCoinAlarmRepository;
import com.bit.kodari.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RegisterCoinAlarmService {
    @Autowired
    RegisterCoinAlarmRepository registerCoinAlarmRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public RegisterCoinAlarmService(RegisterCoinAlarmRepository registerCoinAlarmRepository, JwtService jwtService) {
        this.registerCoinAlarmRepository = registerCoinAlarmRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

}
