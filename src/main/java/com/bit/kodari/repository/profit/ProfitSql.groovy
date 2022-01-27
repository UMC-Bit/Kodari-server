package com.bit.kodari.repository.profit

class ProfitSql {


    public static final String INSERT = """
        INSERT INTO Profit (accountIdx, profitRate, earning)
        Values (:accountIdx ,:profitRate , :earning)
"""

    public static final String FIND_BY_PROFITIDX = """
        SELECT * from Profit where profitIdx = :profitIdx
        WHERE status = "active"

"""

    public static final String FIND_BY_TRADEIDX = """
			SELECT * from Trade where tradeIdx = :tradeIdx
"""


    // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
    public static final String FIND_BY_ACCOUNTIDX = """
         SELECT profitIdx, accountIdx, TRUNCATE(profitRate,2) profitRate, TRUNCATE(earning,0) earning, status, createAt
         FROM Profit
         WHERE accountIdx = :accountIdx AND status = "active"
         ORDER BY createAt asc
         """


    // Profit 수익내역 조회: 특정 계좌의 코인심볼 전체조회
    public static final String FIND_SYMBOL_BY_ACCOUNTIDX="""
        SELECT U.amount, C.symbol, A.property, A.totalProperty
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


    //  profit인덱스로 해당 유저인덱스 조회
    public static final String FIND_STATUS_BY_PROFITIDX = """
        SELECT status
        FROM Profit
        WHERE profitIdx = :profitIdx

"""


    // Profit 수익내역 삭제: 특정 계좌의 수익내역 삭제
    public static final String DELETE ="""
        UPDATE Profit SET status="inactive" WHERE profitIdx = :profitIdx
"""


    // 수익내역 삭제 : 전체삭제
    public static final String DELETE_ALL = """
			DELETE P from Profit as P right join Account as A ON P.accountIdx = A.accountIdx
            WHERE A.userIdx = :userIdx;
"""




}

