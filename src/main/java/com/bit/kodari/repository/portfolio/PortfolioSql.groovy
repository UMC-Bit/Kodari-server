package com.bit.kodari.repository.portfolio;

class PortfolioSql {
    //포트폴리오 등록
    public static final String INSERT = """
			INSERT INTO Portfolio (userIdx, accountIdx)
			values (:userIdx, :accountIdx)
	"""

    //대표 코인 등록 - 비트코인, 이더리움, 솔라나
    public static final String INSERT_REPRESENT = """
            INSERT INTO Represent (portIdx, coinIdx)
            values (:portIdx, :coinIdx)
    """

    //포트폴리오 조회 - portIdx, accountIdx, accountName, userIdx, marketIdx, userCoinIdx, coinName, coinImg, proprerty, priceAvg, amount
    public static final String GET_PORTFOLIO = """
			select p.portIdx, a.accountIdx, a.accountName, a.property, a.totalProperty, p.userIdx, m.marketName
            from Portfolio p
                join (select accountIdx, accountName, property, totalProperty, marketIdx, status from Account) as a on a.accountIdx = p.accountIdx
                join (select marketIdx, marketName, status from Market) as m on m.marketIdx = a.marketIdx
            WHERE p.status = 'active'
            AND a.status = 'active'
            AND m.status = 'active'
            AND p.portIdx = :portIdx
    """

    //소유코인 가져오기
    public static final String GET_USER_COIN = """
        select u.userCoinIdx, u.userIdx, u.coinIdx, c.coinName, c.symbol, c.coinImg, u.accountIdx, u.priceAvg, u.amount, u.status from UserCoin as u
        join (select coinIdx, coinName, coinImg, symbol from Coin) as c on c.coinIdx = u.coinIdx
        where u.accountIdx = :accountIdx and u.status = 'active'
    """

    //포트폴리오 삭제 - 소유코인, 계좌 다 삭제되도록 (모두 있을 때)
    public static final String DELETE = """
			UPDATE Portfolio AS p, UserCoin AS u, Account AS a
			SET p.status = 'inactive', u.status = 'inactive', a.status = 'inactive' 
			WHERE p.portIdx = :portIdx
			AND u.accountIdx = :accountIdx
			AND a.accountIdx = :accountIdx
    """

    //포트폴리오 삭제 - 계좌 다 삭제되도록 (userCoin 없을 때)
    public static final String DELETE_TWO = """
			UPDATE Portfolio AS p, Account AS a
			SET p.status = 'inactive', a.status = 'inactive' 
			WHERE p.portIdx = :portIdx
			AND a.accountIdx = :accountIdx
    """

    //대표 코인 삭제
    public static final String DELETE_REPRESENT = """
			DELETE FROM Represent WHERE portIdx = :portIdx
    """

    //accountIdx로 계좌 status 가져오기
    public static final String GET_ACCOUNT_STATUS ="""
        SELECT status from Account where accountIdx = :accountIdx
    """;

    //portIdx로 userIdx 가져오기
    public static final String GET_USER_IDX ="""
        SELECT userIdx from Portfolio where portIdx = :portIdx
    """;

    //portIdx로 accountIdx 가져오기
    public static final String GET_ACCOUNT_IDX ="""
        SELECT accountIdx from Portfolio where portIdx = :portIdx
    """;

    // 모든 포트폴리오 가져오기
    public static final String GET_ALL_PORTFOLIO ="""
        SELECT userIdx, accountIdx from Portfolio where status = 'active'
    """;

    //userIdx로 모든 portIdx 가져오기
    public static final String GET_ALL_PORT_IDX ="""
        SELECT portIdx from Portfolio where userIdx = :userIdx and status = 'active'
    """;

    //accountIdx로 모든 userCoinIdx 가져오기
    public  static final String GET_USER_COIN_IDX = """
        SELECT userCoinIdx from UserCoin where accountIdx = :accountIdx and status = 'active'
    """

    //accountIdx로 계좌 userIdx 가져오기
    public static final String GET_ACCOUNT_USER ="""
        SELECT userIdx from Account where accountIdx = :accountIdx
    """;
}
