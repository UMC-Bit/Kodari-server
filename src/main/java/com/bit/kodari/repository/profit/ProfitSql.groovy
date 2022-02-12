package com.bit.kodari.repository.profit

class ProfitSql {


    public static final String INSERT = """
        INSERT INTO Profit (accountIdx, profitRate, earning, createAt)
        Values (:accountIdx ,:profitRate , :earning, :date)
"""

    // 과거 거래내역 생성
    public static final String INSERT_PREV = """
        INSERT INTO Profit (accountIdx, profitRate, earning, createAt)
        Values (:accountIdx ,:profitRate , :earning, :prevDate);
"""

    public static final String FIND_BY_PROFITIDX = """
        SELECT * from Profit
        WHERE profitIdx = :profitIdx AND status = 'active'

"""

    public static final String FIND_BY_TRADEIDX = """
			SELECT * from Trade where tradeIdx = :tradeIdx
"""


    // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
    public static final String FIND_BY_ACCOUNTIDX = """
         SELECT profitIdx, accountIdx, TRUNCATE(profitRate,2) profitRate, TRUNCATE(earning,0) earning, status, createAt
         FROM Profit
         WHERE accountIdx = :accountIdx AND status = 'active'
         ORDER BY createAt asc
         """

    // Profit 수익내역 일별 조회: 특정 계좌의 수익내역 조회
    public static final String FIND_DAILY_BY_ACCOUNTIDX = """
# 특정 계좌의 일 단위 수익(손익금, 수익률) 전체 조회
SELECT profitIdx, accountIdx, TRUNCATE(profitRate,2) profitRate, TRUNCATE(earning,0) earning, status, createAt,DATE(`createAt`) AS `date` # DATE : 일 까지 조회
FROM Profit
WHERE accountIdx = :accountIdx AND status = 'active'
GROUP BY date # 날짜 별로 묶음 중에서 하루 중 제일 빠른 것을 조회
ORDER BY date asc;
         """

    // Profit 수익내역 주 별 조회: 특정 계좌의 전체 수익내역 조회
    public static final String FIND_WEEKLY_BY_ACCOUNTIDX = """
# 특정 계좌의 주 별 수익(손익금, 수익률) 전체 조회
SELECT profitIdx, accountIdx, TRUNCATE(profitRate,2) profitRate, TRUNCATE(earning,0) earning, status, createAt,CONCAT(SUBSTR(YEARWEEK(`createAt`) FROM 1 FOR 4),'-',SUBSTR(YEARWEEK(`updateAt`),5)) AS `week` # DATE : 일 까지 조회
FROM Profit
WHERE accountIdx = :accountIdx AND status = 'active'
GROUP BY week # 주 별로 묶음 중에서 하루 중 제일 빠른 것을 조회
ORDER BY week asc;
         """

    // Profit 수익내역 월 별 조회: 특정 계좌의 전체 수익내역 조회
    public static final String FIND_MONTHLY_BY_ACCOUNTIDX = """
# 특정 계좌의 월 별 수익(손익금, 수익률) 전체 조회
SELECT profitIdx, accountIdx, TRUNCATE(profitRate,2) profitRate, TRUNCATE(earning,0) earning, status, createAt,DATE_FORMAT(`createAt`,'%Y-%m') AS `month` # DATE : 일 까지 조회
FROM Profit
WHERE accountIdx = :accountIdx AND status = 'active'
GROUP BY month # 월 별로 묶음 중에서 하루 중 제일 빠른 것을 조회
ORDER BY month asc;
         """

    // Profit 수익내역 조회: 특정 계좌의 코인심볼 전체조회
    public static final String FIND_SYMBOL_BY_ACCOUNTIDX="""
        SELECT U.amount,U.priceAvg, C.symbol, A.property, A.totalProperty
        FROM UserCoin as U join Coin as C on U.coinIdx = C.coinIdx join Account as A on U.accountIdx = A.accountIdx
        WHERE U.accountIdx = :accountIdx
        
"""


    //  계좌인덱스로 해당 유저인덱스 조회
    public static final String FIND_USERIDX_BY_ACCOUNTIDX = """
        SELECT userIdx
        FROM Account
        WHERE accountIdx = :accountIdx

"""

    //  profit인덱스로 해당 유저인덱스 조회
    public static final String FIND_USERIDX_BY_PROFITIDX = """
        SELECT A.userIdx
        FROM Profit as P join Account as A on P.accountIdx= A.accountIdx
        WHERE profitIdx = :profitIdx

"""


    // 수익 조회: 수익내역의 전체 accountIdx 중복제거 조회
    public static final String FIND_ACCOUNTIDX = """
SELECT DISTINCT A.accountIdx # DISTINCT : 중복제거
FROM Profit as P INNER JOIN Account as A ON P.accountIdx = A.accountIdx
WHERE P.status = 'active';

"""


    //  profit인덱스로 해당 유저인덱스 조회
    public static final String FIND_STATUS_BY_PROFITIDX = """
        SELECT status
        FROM Profit
        WHERE profitIdx = :profitIdx

"""


    // Profit 수익내역 삭제: 특정 계좌의 수익내역 삭제
    public static final String DELETE ="""
        UPDATE Profit SET status='inactive' WHERE profitIdx = :profitIdx
"""


    // 수익내역 삭제 : 전체삭제
    public static final String DELETE_ALL = """
			DELETE P from Profit as P right join Account as A ON P.accountIdx = A.accountIdx
            WHERE A.userIdx = :userIdx;
"""

    //Profit 수익내역 삭제:  Trade연동 수익내역 삭제 및 새로 생성
    public static final String DELETE_BY_USERCOINIDX_DATE="""
        # Profit 수익내역 삭제:  Trade연동 수익내역 삭제 
        UPDATE Profit as P
        INNER JOIN UserCoin as UC on P.accountIdx=UC.accountIdx
        SET P.status='inactive'
        WHERE UC.userCoinIdx = :userCoinIdx AND P.status = 'active' AND P.createAt > :date;
"""




}

