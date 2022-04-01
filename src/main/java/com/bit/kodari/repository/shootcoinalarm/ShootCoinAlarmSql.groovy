package com.bit.kodari.repository.shootcoinalarm

class ShootCoinAlarmSql {

    //코인 시세 알림 등록
    public static final String INSERT_COIN_ALARM = """
        INSERT INTO ShootCoinAlarm (userIdx, marketIdx, coinIdx, growth, decline)
        values (:userIdx, :marketIdx, :coinIdx, :growth, :decline)
        """

    //등록된 코인당 시세 알림 수
    public static final String GET_REGISTER_ALARM_COUNT = """
        SELECT COUNT(ifnull(shootCoinAlarmIdx,0)) AS 'alarm_count'
        FROM ShootCoinAlarm
        WHERE userIdx = :userIdx and marketIdx = :marketIdx and coinIdx = :coinIdx and status = 'active'
        """

    //market별 등록된 코인 시세 알림 수
    public static final String GET_REGISTER_MARKET_ALARM_COUNT = """
        SELECT COUNT(DISTINCT coinIdx) AS 'coin_count'
        FROM ShootCoinAlarm
        WHERE userIdx = :userIdx and marketIdx = :marketIdx and status = 'active'
        """

    //같은 값의 알림 존재 여부
    public static final String GET_EXIST_ALARM = """
        SELECT if(shootCoinAlarmIdx, true, false) as 'exist'
        FROM ShootCoinAlarm
        WHERE userIdx = :userIdx and marketIdx = :marketIdx and coinIdx = :coinIdx and growth = :growth and decline = :decline
        """

    //코인 시세 알림 수정
    public static final String UPDATE_REGISTER_COIN_ALARM = """
        UPDATE ShootCoinAlarm SET growth = :growth, decline = :decline
        WHERE shootCoinAlarmIdx = :shootCoinAlarmIdx
        """

    //registerCoinAlarmIdx로 userIdx 구하기
    public static final String GET_USER_IDX = """
        SELECT userIdx from ShootCoinAlarm WHERE shootCoinAlarmIdx = :shootCoinAlarmIdx 
        """

    //registerCoinAlarmIdx로 marketIdx 구하기
    public static final String GET_MARKET_IDX = """
        SELECT marketIdx from ShootCoinAlarm WHERE shootCoinAlarmIdx = :shootCoinAlarmIdx 
        """

    //registerCoinAlarmIdx로 coinIdx 구하기
    public static final String GET_COIN_IDX = """
        SELECT coinIdx from ShootCoinAlarm WHERE shootCoinAlarmIdx = :shootCoinAlarmIdx 
        """

    //코인 시세 알림 삭제
    public static final String DELETE_ALARM = """
        UPDATE ShootCoinAlarm SET status = 'inactive' WHERE shootCoinAlarmIdx = :shootCoinAlarmIdx
        """

    //유저별 폭락, 폭등 알림 조회
    public static final String LIST_COIN_ALARM = """
        SELECT userIdx
        FROM ShootCoinAlarm
        WHERE userIdx = :userIdx and status = 'active'
        GROUP BY userIdx
        """

    //폭락, 폭등 알림 조회 시 마켓 정보 조회
    public static final String LIST_MARKET = """
        SELECT m.marketIdx, m.marketName
        FROM ShootCoinAlarm as s join Market as m on s.marketIdx = m.marketIdx
        WHERE s.userIdx = :userIdx and s.status = 'active'
        GROUP BY s.marketIdx
        """

    //폭락, 폭등 알림 조회 시 코인 정보 조회
    public static final String LIST_COIN = """
        SELECT c.coinIdx, c.coinName, c.symbol, c.coinImg
        FROM ShootCoinAlarm as s join Coin as c on s.coinIdx = c.coinIdx
        WHERE s.userIdx = :userIdx and s.marketIdx = :marketIdx and s.status = 'active'
        GROUP BY s.coinIdx
        """

    //폭락, 폭등 알림 조회 시 해당 알림 정보 조회
    public static final String LIST_ALARM = """
        SELECT shootCoinAlarmIdx, growth, decline
        FROM ShootCoinAlarm
        WHERE userIdx = :userIdx and marketIdx = :marketIdx and coinIdx = :coinIdx and growth > 0.0 and decline > 0.0 and status = 'active'
        """


}
