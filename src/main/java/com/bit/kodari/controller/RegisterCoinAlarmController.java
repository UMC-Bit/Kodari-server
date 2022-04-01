package com.bit.kodari.controller;


import com.bit.kodari.repository.registerCoinAlarm.RegisterCoinAlarmRepository;
import com.bit.kodari.service.RegisterCoinAlarmService;
import com.bit.kodari.utils.JwtService;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/registercoinalarm")
public class RegisterCoinAlarmController {
    @Autowired
    RegisterCoinAlarmService registerCoinAlarmService;
    @Autowired
    RegisterCoinAlarmRepository registerCoinAlarmRepository;
    @Autowired
    private final JwtService jwtService;

    public RegisterCoinAlarmController(RegisterCoinAlarmService registerCoinAlarmService, JwtService jwtService) {
        this.registerCoinAlarmService = registerCoinAlarmService;
        this.jwtService = jwtService;
    }

}
