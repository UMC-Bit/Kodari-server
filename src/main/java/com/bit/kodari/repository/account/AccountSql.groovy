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


}
