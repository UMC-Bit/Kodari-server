package com.bit.kodari.repository.portfolio;

class PortfolioSql {
    //포트폴리오 등록
    public static final String INSERT = """
			INSERT INTO Portfolio (userIdx, accountIdx)
			values (:userIdx, :accountIdx)
	"""

    //포트폴리오 조회 - portIdx, accountIdx, accountName, userIdx, marketIdx, userCoinIdx, coinName, coinImg, proprerty, priceAvg, amount
    public static final String GET_PORTFOLIO = """
			select p.portIdx, a.accountIdx, a.accountName, a.property, p.userIdx, m.marketName, u.userCoinIdx, c.coinName, c.coinImg, u.priceAvg, u.amount
            from Portfolio p
                join (select accountIdx, accountName, property, marketIdx, status from Account) as a on a.accountIdx = p.accountIdx
                join (select userCoinIdx, accountIdx, coinIdx, priceAvg, amount, status from UserCoin) as u on u.accountIdx = p.accountIdx
                join (select marketIdx, marketName, status from Market) as m on m.marketIdx = a.marketIdx
                join (select coinIdx, coinName, coinImg, status from Coin) as c on c.coinIdx = u.coinIdx
            WHERE p.status = 'active'
            AND a.status = 'active'
            AND u.status = 'active'
            AND m.status = 'active'
            AND c.status = 'active'
            AND p.portIdx = :portIdx
    """

    //포트폴리오 삭제 - 소유코인, 계좌 다 삭제되도록
    //대표코인
    public static final String DELETE = """
			UPDATE Portfolio AS p, UserCoin AS u, Account AS a 
			SET p.status = 'inactive', u.status = 'inactive', a.status = 'inactive' 
			WHERE p.portIdx = :portIdx
			AND u.accountIdx = :accountIdx
			AND a.accountIdx = :accountIdx
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
}
