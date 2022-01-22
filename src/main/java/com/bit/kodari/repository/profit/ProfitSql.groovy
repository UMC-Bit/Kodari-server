package com.bit.kodari.repository.profit

class ProfitSql {


    public static final String INSERT = """
        INSERT INTO Profit (accountIdx, profitRate, earning)
        Values (:accountIdx ,:profitRate , :earning)
"""

    public static final String FIND_BY_PROFITIDX = """
        SELECT * from Profit where profitIdx = :profitIdx

"""

    public static final String FIND_BY_TRADEIDX = """
			SELECT * from Trade where tradeIdx = :tradeIdx
"""


    // Profit 수익내역 조회: 특정 계좌의 수익내역 조회
    public static final String FIND_BY_ACCOUNTIDX = """
         SELECT * 
         FROM Profit
         WHERE accountIdx = :accountIdx
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

    // Profit 수익내역 삭제: 특정 계좌의 수익내역 삭제
    public static final String DELETE ="""
        UPDATE Profit SET status="inactive" WHERE profitIdx = :profitIdx
"""




}

