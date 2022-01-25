package com.bit.kodari.repository.trade

class TradeSql {
    public static final String INSERT = """
			INSERT INTO Trade (portIdx,coinIdx,price, amount, fee, category, memo, date)
			values (:portIdx, :coinIdx,  :price, :amount, :fee, :category, :memo, :date)
			"""


    public static final String FIND_BY_TRADEIDX = """
			SELECT * from Trade where tradeIdx = :tradeIdx
"""

    public static final String FIND_LAST_INSERT_ID = """
			SELECT last_insert_id()
"""

    //거래내역 조회: 특정 포트폴리오의 특정 코인의 거래내역
    public static final String FIND_BY_PORTIDX_COINIDX = """
         SELECT t.tradeIdx, c.coinName, t.price, t.amount, t.fee, t.category, t.memo, t.date, t.status 
         FROM Trade as t join Portfolio as p on t.portIdx = p.portIdx join Coin as c on t.coinIdx = c.coinIdx 
         WHERE t.portIdx = :portIdx AND t.coinIdx = :coinIdx AND t.status = "active"
         ORDER BY t.date desc,t.tradeIdx desc
         """

    //유저 인덱스 조회:
    public static final String FIND_USERIDX_BY_TRADEIDX = """
         SELECT p.userIdx
         FROM Trade as t join Portfolio as p on t.portIdx = p.portIdx join Coin as c on t.coinIdx = c.coinIdx 
         WHERE t.tradeIdx = :tradeIdx 
         """

    //유저 인덱스 조회: 거래내역 생성 시 포트폴리오인덱스로 한명만 조회
    public static final String FIND_USERIDX_BY_PORTIDX = """
         SELECT p.userIdx
         FROM Portfolio as p
         WHERE p.portIdx = :portIdx
         """


    // Trade인덱스로 status 조회
    public static final String FIND_STATUS_BY_TRADEIDX = """
			SELECT status
			FROM Trade 
			WHERE tradeIdx = :tradeIdx
"""




    // 거래내역 수정 : 코인 가격 수정(Patch)
    public static final String UPDATE_PRICE = """
			UPDATE Trade SET price = :price WHERE tradeIdx = :tradeIdx
"""

    // 거래내역 수정 : 코인 갯수 수정(Patch)
    public static final String UPDATE_AMOUNT = """
			UPDATE Trade SET amount = :amount WHERE tradeIdx = :tradeIdx
"""

    // 거래내역 수정 : 수수료 수정(Patch)
    public static final String UPDATE_FEE = """
			UPDATE Trade SET fee = :fee WHERE tradeIdx = :tradeIdx
"""

    // 거래내역 수정 : 매수/매도 수정(Patch)
    public static final String UPDATE_CATEGORY = """
			UPDATE Trade SET category = :category WHERE tradeIdx = :tradeIdx
"""

    // 거래내역 수정 : 메모 수정(Patch)
    public static final String UPDATE_MEMO = """
			UPDATE Trade SET memo = :memo WHERE tradeIdx = :tradeIdx
"""

    // 거래내역 수정 : 거래시각 수정(Patch)
    public static final String UPDATE_DATE = """
			UPDATE Trade SET date = :date WHERE tradeIdx = :tradeIdx
"""


    // 거래내역 삭제 : status 수정
    public static final String DELETE = """
			UPDATE Trade SET status = "inactive" WHERE tradeIdx = :tradeIdx
"""


    // 거래내역 삭제 : 전체삭제
    public static final String DELETE_ALL = """
			DELETE FROM Trade as T join Port as P on T.portIdx = P.portIdx
			WHERE userIdx = :userIdx;
"""


}
