package com.bit.kodari.repository.registerCoinAlarm

class RegisterCoinAlarmSql {

    /*
    특정 코인 지정가격 조회: marketIdx, coinIdx 로 조회information_schema
     */
    public static final String FIND_BY_MARKETIDX_COINIDX = """
    # 특정 코인 지정가격 조회: marketIdx, coinIdx 로 조회
    SELECT registerCoinAlarmIdx, userIdx, marketIdx, coinIdx, targetPrice, status
    FROM RegisterCoinAlarm
    WHERE status = 'active' AND marketIdx = :marketIdx AND coinIdx = :coinIdx;
    """

}