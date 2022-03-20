package com.bit.kodari.repository.account

class AccountSql {
    //계좌 등록
    public static final String INSERT = """
			INSERT INTO Account (accountName, userIdx, marketIdx, property, totalProperty)
			values (:accountName, :userIdx, :marketIdx, :property, :property)
			"""

    //유저 계좌 조회
    public static final String FIND_USER_ACCOUNT = """
			SELECT accountIdx, accountName, userIdx, marketIdx, concat(format(property, 0), '원') as property, totalProperty, status FROM Account WHERE userIdx = :userIdx AND status = 'active'
			"""

    //유저 계좌 단일 조회
    public static final String FIND_ACCOUNT_BY_ACCOUNT_IDX = """
			SELECT accountName, marketIdx, concat(format(property, 0), '원') as property, totalProperty, status FROM Account WHERE accountIdx = :accountIdx AND status = 'active'
			"""

    //총자산 수정
    public static final String UPDATE_TOTAL_PROPERTY = """
			UPDATE Account SET totalProperty = :totalProperty WHERE accountIdx = :accountIdx
    """

    //계좌 이름 수정
    public static final String UPDATE_ACCOUNT_NAME = """
			UPDATE Account SET accountName = :accountName WHERE accountIdx = :accountIdx
    """

    //현금 자산 수정
    public static final String UPDATE_PROPERTY = """
			UPDATE Account SET property = :property, totalProperty = :totalProperty WHERE accountIdx = :accountIdx 
    """

    //Trade - 현금 자산 수정
    public static final String UPDATE_TRADE_PROPERTY = """
			UPDATE Account SET property = :property WHERE accountIdx = :accountIdx
    """

    // 총 자산 수정 - 업데이트 버튼 누를때마다
    public static final String UPDATE_TOTAL_PROPERTY_BUTTON = """
			UPDATE Account SET totalProperty = :totalProperty WHERE accountIdx = :accountIdx
    """

    //현금 자산 조회
    public static final String FIND_PROPERTY = """
			SELECT accountIdx, userIdx, concat(format(property, 0), '원') as property, totalProperty, status FROM Account WHERE accountIdx = :accountIdx
			"""

    //계좌 삭제
    //포트폴리오랑 소유코인도 다 삭제되게 바꾸기
    public static final String DELETE = """
			UPDATE UserCoin AS u, Portfolio AS p, Account AS a 
			SET u.status = 'inactive', p.status = 'inactive', a.status = 'inactive' 
			WHERE u.accountIdx = :accountIdx
			AND p.accountIdx = :accountIdx
			AND a.accountIdx = :accountIdx
    """

    //계좌 삭제 - 계좌랑 포트폴리오
    public static final String DELETE_TWO = """
			UPDATE Portfolio AS p, Account AS a 
			SET p.status = 'inactive', a.status = 'inactive' 
			WHERE p.accountIdx = :accountIdx
			AND a.accountIdx = :accountIdx
    """

    // 계좌 삭제: 전체삭제
    public static final String DELETE_ALL = """
			DELETE FROM Account
            WHERE userIdx = :userIdx;
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

    //accountIdx로 marketIdx 가져오기
    public static final String GET_MARKET_IDX_BY_ACCOUNT ="""
        SELECT marketIdx from Account where accountIdx = :accountIdx
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

    // portIdx로 userIdx 가져오기
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

    //accountIdx로 accountName 가져오기
    public static final String GET_NAME_BY_ACCOUNT_IDX = """
        SELECT accountName from Account where accountIdx = :accountIdx
    """

    //accountIdx로 totalProperty 가져오기
    public static final String GET_TOTAL_BY_ACCOUNT_IDX = """
        SELECT totalProperty from Account where accountIdx = :accountIdx
    """

    // userIdx, accountIdx로 UserCoin 가져오기
    public static final String GET_USER_COIN ="""
        SELECT priceAvg, amount from UserCoin where userIdx = :userIdx AND accountIdx = :accountIdx
    """;

    //userIdx랑 marketIdx로 accountIdx 가져오기
    public static final String GET_ACCOUNT_IDX_THREE ="""
        SELECT accountIdx from Account where userIdx = :userIdx and marketIdx = :marketIdx AND status = 'active'
    """;

    //accountIdx로 모든 userCoinIdx 가져오기
    public  static final String GET_USER_COIN_IDX_LIST = """
        SELECT userCoinIdx from UserCoin where accountIdx = :accountIdx and status = 'active'
    """

    //accountIdx로 거래 생성시각 조회
    public static final String GET_CREATEAT ="""
        SELECT createAt 
        FROM Account 
        WHERE accountIdx = :accountIdx AND status='active'
"""

}
