package com.bit.kodari.repository.portfolio;

class PortfolioSql {
    //포트폴리오 등록
    public static final String INSERT = """
			INSERT INTO Portfolio (userIdx, accountIdx)
			values (:userIdx, :accountIdx)
	"""

    //포트폴리오 조회
    public static final String GET_PORTFOLIO = """
			SELECT Portfolio SET status = 'inactive' WHERE portIdx = :portIdx
			SELECT c.coinName, u.userIdx, u.priceAvg, u.amount, u.status FROM UserCoin u join (select coinIdx, coinName from Coin) as c on c.coinIdx = u.coinIdx WHERE u.userIdx = :userIdx AND u.status = 'active'
    """

    //포트폴리오 삭제
    public static final String DELETE = """
			UPDATE Portfolio SET status = 'inactive' WHERE portIdx = :portIdx
    """

    //accountIdx로 계좌 status 가져오기
    public static final String GET_ACCOUNT_STATUS ="""
        SELECT status from Account where accountIdx = :accountIdx
    """;

    //portIdx로 userIdx 가져오기
    public static final String GET_USER_IDX ="""
        SELECT userIdx from Portfolio where portIdx = :portIdx
    """;

    // 모든 포트폴리오 가져오기
    public static final String GET_ALL_PORTFOLIO ="""
        SELECT userIdx, accountIdx from Portfolio where status = 'active'
    """;
}
