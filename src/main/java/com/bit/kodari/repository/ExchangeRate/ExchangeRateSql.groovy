package com.bit.kodari.repository.ExchangeRate

class ExchangeRateSql {
    public static final String FIND_ALL="""
# 환율 전체 조회
SELECT *
FROM ExchangeRate
WHERE status='active';
"""

    // 환율 업데이트: ExchangeRateApi를 이용해 현재 환율 시세로 업데이트
    public static final String UPDATE_EXCHANGEPRICE="""
#환율 가격 업데이트
UPDATE ExchangeRate as E
SET E.exchangePrice= :exchangePrice
WHERE E.money='미국 USD' AND E.status='active';
"""

}
