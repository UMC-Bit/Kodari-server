package com.bit.kodari.repository.registerCoinAlarm

class RegisterCoinAlarmSql {

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

    //같은 값의 알림 존재 여부
    public static final String GET_EXIST_ALARM = """
        SELECT if(registerCoinAlarmIdx, true, false) as 'exist'
        FROM RegisterCoinAlarm
        WHERE userIdx = :userIdx and marketIdx = :marketIdx and coinIdx = :coinIdx and targetPrice = :targetPrice
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

    //registerCoinAlarmIdx로 marketIdx 구하기
    public static final String GET_MARKET_IDX = """
        SELECT marketIdx from RegisterCoinAlarm WHERE registerCoinAlarmIdx = :registerCoinAlarmIdx 
        """

    //registerCoinAlarmIdx로 marketIdx 구하기
    public static final String GET_COIN_IDX = """
        SELECT coinIdx from RegisterCoinAlarm WHERE registerCoinAlarmIdx = :registerCoinAlarmIdx 
        """

    //코인 시세 알림 삭제
    public static final String DELETE_ALARM = """
        UPDATE RegisterCoinAlarm SET status = 'inactive' WHERE registerCoinAlarmIdx = :registerCoinAlarmIdx
        """

    //유저별 코인 시세 알림 조회
    public static final String LIST_COIN_ALARM = """
        SELECT userIdx
        FROM RegisterCoinAlarm
        WHERE userIdx = :userIdx and status = 'active'
        GROUP BY userIdx
        """

    //코인 시세 알림 조회 시 마켓 정보 조회
    public static final String LIST_MARKET = """
        SELECT m.marketIdx, m.marketName
        FROM RegisterCoinAlarm as r join Market as m on r.marketIdx = m.marketIdx
        WHERE r.userIdx = :userIdx and r.status = 'active'
        GROUP BY r.marketIdx
        """

    //코인 시세 알림 조회 시 코인 정보 조회
    public static final String LIST_COIN = """
        SELECT c.coinIdx, c.coinName, c.symbol, c.coinImg
        FROM RegisterCoinAlarm as r join Coin as c on r.coinIdx = c.coinIdx
        WHERE r.userIdx = :userIdx and r.marketIdx = :marketIdx and r.status = 'active'
        GROUP BY r.coinIdx
        """

    //코인 시세 알림 조회 시 해당 알림 정보 조회
    public static final String LIST_ALARM = """
        SELECT registerCoinAlarmIdx, targetPrice
        FROM RegisterCoinAlarm 
        WHERE userIdx = :userIdx and marketIdx = :marketIdx and coinIdx = :coinIdx and status = 'active'
        """


}
