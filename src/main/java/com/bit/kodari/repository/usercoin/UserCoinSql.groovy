package com.bit.kodari.repository.usercoin;

class UserCoinSql {
    //소유 코인 등록
    public static final String INSERT = """
			INSERT INTO UserCoin (userIdx, coinIdx, accountIdx, priceAvg, amount)
			values (:userIdx, :coinIdx, :accountIdx, :priceAvg, :amount)
			"""

    //특정 소유 코인 조회
    public static final String FIND_USER_COIN_IDX = """
			SELECT u.userCoinIdx, c.coinIdx, c.coinName, c.symbol, c.coinImg, c.twitter, u.userIdx, u.priceAvg, u.amount, u.status FROM UserCoin u join (select coinIdx, coinName, symbol, coinImg, twitter from Coin) as c on c.coinIdx = u.coinIdx WHERE u.userCoinIdx = :userCoinIdx AND u.status = 'active'
			"""

    //소유 코인 조회
    //coinName, userIdx, priceAvg, amount, status
    //select c.coinName, u.userIdx, u.userCoinIdx from UserCoin u join Coin c where u.coinIdx = c.coinIdx;
    //concat(format(u.priceAvg, 0), '원') as priceAvg
    //format(u.priceAvg, 0) as priceAvg
    public static final String FIND_USER_COIN = """
			SELECT p.portIdx, u.userCoinIdx, c.coinIdx, c.coinName, c.symbol, c.coinImg, c.twitter, u.userIdx, u.priceAvg, u.amount, u.status FROM UserCoin u 
			join (select coinIdx, coinName, symbol, coinImg, twitter from Coin) as c on c.coinIdx = u.coinIdx 
			join Account as a on a.accountIdx = u.accountIdx
			join Portfolio as p on p.accountIdx = a.accountIdx
			WHERE p.portIdx = :portIdx AND u.status = 'active'
			"""

    //소유 코인 수정
    public static final String UPDATE_USER_COIN = """
			UPDATE UserCoin SET priceAvg = :priceAvg, amount = :amount WHERE userCoinIdx = :userCoinIdx
    """

    //소유 코인 삭제
    public static final String DELETE = """
			UPDATE UserCoin SET status = 'inactive' WHERE userCoinIdx = :userCoinIdx
    """

    //전체 소유 코인 삭제
    public static final String ALL_DELETE = """
			UPDATE UserCoin SET status = 'inactive' WHERE userIdx = :userIdx
    """

    // 소유 코인 삭제: 전체삭제
    public static final String DELETE_ALL = """
			DELETE FROM UserCoin
            WHERE userIdx = :userIdx;
"""

    // Trade - 매수, 매도 계산(매수평단가), 수수료 0.05%
    public static final String PRICE_AVERAGE = """
			UPDATE UserCoin SET priceAvg = :priceAvg, amount = :amount WHERE userCoinIdx = :userCoinIdx
    """

    //userCoinIdx로 userIdx 가져오기
    public static final String GET_USER_IDX ="""
        SELECT userIdx from UserCoin where userCoinIdx = :userCoinIdx
    """;

    //userCoinIdx로 accountIdx 가져오기
    public static final String GET_ACCOUNT_IDX ="""
        SELECT accountIdx from UserCoin where userCoinIdx = :userCoinIdx
    """;

    //accountIdx로 계좌 status 가져오기
    public static final String GET_ACCOUNT_STATUS ="""
        SELECT status from Account where accountIdx = :accountIdx
    """;

    //portIdx로 userIdx 가져오기
    public static final String GET_USER_IDX_BY_PORT ="""
        SELECT userIdx from Portfolio where portIdx = :portIdx and status = 'active'
    """;

    /**
     * 매수평가단 계산에 필요함
     */
    // userCoinIdx로 coinIdx 가져오기
    public static final String GET_COIN_IDX ="""
        SELECT coinIdx from UserCoin where userCoinIdx = :userCoinIdx
    """;

    // accountIdx로 portIdx 가져오기
    public static final String GET_PORT_IDX ="""
        SELECT portIdx from Portfolio where accountIdx = :accountIdx
    """;

    // coinIdx, portIdx로 trade 테이블의 가장 최신 tradeIdx 가져오기
    public static final String GET_TRADE_IDX ="""
        SELECT tradeIdx from Trade where coinIdx = :coinIdx AND portIdx = :portIdx order by createAt desc limit 1
    """;

    //tradeIdx로 trade 테이블의 category 가져오기
    public static final String GET_TRADE_CATEGORY ="""
        SELECT category from Trade where tradeIdx = :tradeIdx
    """;

    //tradeIdx로 trade 테이블의 price 가져오기
    public static final String GET_TRADE_PRICE ="""
        SELECT price from Trade where tradeIdx = :tradeIdx
    """;

    //tradeIdx로 trade 테이블의 amount 가져오기
    public static final String GET_TRADE_AMOUNT ="""
        SELECT amount from Trade where tradeIdx = :tradeIdx
    """;

    //tradeIdx로 trade 테이블의 fee 가져오기
    public static final String GET_TRADE_FEE ="""
        SELECT fee from Trade where tradeIdx = :tradeIdx
    """;

    //userCoinIdx로 priceAvg 가져오기
    public static final String GET_PRICE_AVG ="""
        SELECT priceAvg from UserCoin where userCoinIdx = :userCoinIdx
    """;

    //userCoinIdx로 UserCoin 테이블의 amount 가져오기
    public static final String GET_USER_COIN_AMOUNT ="""
        SELECT amount from UserCoin where userCoinIdx = :userCoinIdx
    """;

    //accountIdx로 Account의 totalProperty 가져오기
    public static final String GET_TOTAL_PROPERTY ="""
        SELECT totalProperty from Account where accountIdx = :accountIdx
    """;

    //accountIdx로 Account의 property 가져오기
    public static final String GET_PROPERTY ="""
        SELECT property from Account where accountIdx = :accountIdx
    """;

    //accountIdx로 계좌 userIdx 가져오기
    public static final String GET_ACCOUNT_USER ="""
        SELECT userIdx from Account where accountIdx = :accountIdx
    """;

}
