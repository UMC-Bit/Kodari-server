package com.bit.kodari.repository.registerCoinAlarm

import com.bit.kodari.utils.JwtService
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Slf4j
@Service
class RegisterCoinAlarmSql {
    @Autowired
    RegisterCoinAlarmRepository registerCoinAlarmRepository;
    //private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    JwtService jwtService;

    public RegisterCoinAlarmService(RegisterCoinAlarmRepository registerCoinAlarmRepository, JwtService jwtService) {
        this.registerCoinAlarmRepository = registerCoinAlarmRepository;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    //코인 시세 알림 등록
    public static final String INSERT_COIN_ALARM = """
        INSERT INTO RegisterCoinAlarm (userIdx, marketIdx, coinIdx, targetPrice)
        values (:userIdx, :marketIdx, :coinIdx, :targetPrice)
        """

    //등록된 코인당 시세 알림 수
    public static final String GET_REGISTER_ALARM_COUNT = """
        SELECT COUNT(ifnull(registerCoinAlarmIdx,0)) AS 'alarm_count'
        FROM RegisterCoinAlarm
        WHERE userIdx = :userIdx and marketIdx = :marketIdx and coinIdx = :coinIdx and status = 'active'
        """

    //market별 등록된 코인 시세 알림 수
    public static final String GET_REGISTER_MARKET_ALARM_COUNT = """
        SELECT COUNT(DISTINCT coinIdx) AS 'coin_count'
        FROM RegisterCoinAlarm
        WHERE userIdx = :userIdx and marketIdx = :marketIdx and status = 'active'
        """

    //코인 시세 알림 수정
    public static final String UPDATE_REGISTER_COIN_ALARM = """
        UPDATE RegisterCoinAlarm SET targetPrice = :targetPrice
        WHERE registerCoinAlarmIdx = :registerCoinAlarmIdx
        """

    //registerCoinAlarmIdx로 userIdx 구하기
    public static final String GET_USER_IDX = """
        SELECT userIdx from RegisterCoinAlarm WHERE registerCoinAlarmIdx = :registerCoinAlarmIdx 
        """

    //코인 시세 알림 삭제
    public static final String DELETE_ALARM = """
        UPDATE RegisterCoinAlarm SET status = 'inactive' WHERE registerCoinAlarmIdx = :registerCoinAlarmIdx
        """

    //유저별 코인 시세 알림 조회
    public static final String LIST_COIN_ALARM = """
        SELECT rca.registerCoinAlarmIdx, m.marketName, c.coinName, c.symbol, c.coinImg, rca.targetPrice
        FROM RegisterCoinAlarm as rca join User as u on rca.userIdx = u.userIdx
                                      join Market as m on rca.marketIdx = m.marketIdx
                                      join Coin as c on rca.coinIdx = c.coinIdx
        WHERE rca.userIdx = :userIdx and rca.status = 'active'
        ORDER BY rca.marketIdx, rca.coinIdx
        """


}
