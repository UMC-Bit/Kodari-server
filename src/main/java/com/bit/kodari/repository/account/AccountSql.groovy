package com.bit.kodari.repository.account

class AccountSql {
    //계좌 등록
    public static final String INSERT = """
			INSERT INTO Account (accountName, userIdx, marketIdx, property)
			values (:accountName, :userIdx, :marketIdx, :property)
			"""

    //유저 계좌 조회
    public static final String FIND_USER_ACCOUNT = """
			SELECT accountIdx, accountName, userIdx, marketIdx, concat(format(property, 0), '원') as property, status FROM Account WHERE userIdx = :userIdx AND status = 'active'
			"""

    //계좌 이름 수정
    public static final String UPDATE_ACCOUNT_NAME = """
			UPDATE Account SET accountName = :accountName WHERE accountIdx = :accountIdx
    """

    //현금 자산 수정
    public static final String UPDATE_PROPERTY = """
			UPDATE Account SET property = :property WHERE accountIdx = :accountIdx
    """

    //Trade - 현금 자산 수정
    public static final String UPDATE_TRADE_PROPERTY = """
			UPDATE Account SET property = :property WHERE accountIdx = :accountIdx
    """

    //현금 자산 조회
    public static final String FIND_PROPERTY = """
			SELECT accountIdx, concat(format(property, 0), '원') as property, status FROM Account WHERE accountIdx = :accountIdx
			"""

    //계좌 삭제
    public static final String DELETE = """
			UPDATE Account SET status = 'inactive' WHERE accountIdx = :accountIdx
    """

    //accountIdx로 userIdx 가져오기
    public static final String GET_USER_IDX ="""
        SELECT userIdx from Account where accountIdx = :accountIdx
    """;

    //accountIdx로 marketIdx 가져오기
    public static final String GET_MARKET_IDX ="""
        SELECT marketIdx from Account where accountIdx = :accountIdx
    """;

    //userIdx랑 marketIdx로 accountName 가져오기
    public static final String GET_ACCOUNT_NAME ="""
        SELECT accountName from Account where userIdx = :userIdx and marketIdx = :marketIdx AND status = 'active'
    """;

    //accountIdx로 status 가져오기
    public static final String GET_ACCOUNT_STATUS ="""
        SELECT status from Account where accountIdx = :accountIdx
    """;

    // tradeIdx로 portIdx 가져오기
    public static final String GET_PORT_IDX ="""
        SELECT portIdx from Trade where tradeIdx = :tradeIdx
    """;

    // tradeIdx로 coinIdx 가져오기
    public static final String GET_COIN_IDX ="""
        SELECT coinIdx from Trade where tradeIdx = :tradeIdx
    """;

    // portIdx로 accountIdx 가져오기
    public static final String GET_ACCOUNT_IDX ="""
        SELECT accountIdx from Portfolio where portIdx = :portIdx
    """;

    // portIdx로 accountIdx 가져오기
    public static final String GET_USER_IDX_BY_PORT ="""
        SELECT userIdx from Portfolio where portIdx = :portIdx
    """;

    // coinIdx, accountIdx로 userCoinIdx 가져오기
    public static final String GET_USER_COIN_IDX ="""
        SELECT userCoinIdx from UserCoin where coinIdx = :coinIdx AND accountIdx = :accountIdx
    """;

    //accountIdx로 property 가져오기
    public static final String GET_PROPERTY ="""
        SELECT property from Account where accountIdx = :accountIdx
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

}
