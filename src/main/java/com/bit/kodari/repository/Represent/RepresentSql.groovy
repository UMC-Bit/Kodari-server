package com.bit.kodari.repository.Represent

class RepresentSql {
    //대표 코인 등록
    public static final String INSERT = """
			INSERT INTO Represent (portIdx, coinIdx) values (:portIdx, :coinIdx)
	"""

    //대표 코인 조회
    //코인이름, 코인심볼, 코인이미지 추가하기
    public static final String FIND_USER_REPRESENT = """
			SELECT r.representIdx, r.portIdx, r.coinIdx, c.coinName, c.symbol, c.coinImg, r.status FROM Represent r 
			JOIN Coin c on c.coinIdx = r.coinIdx WHERE portIdx = :portIdx AND r.status = 'active'
	"""

    //대표 코인 삭제
    public static final String DELETE = """
			   DELETE FROM Represent WHERE representIdx = :representIdx
    """

    // 대표 코인 삭제 : 전체삭제
    public static final String DELETE_ALL = """
			DELETE R from Represent as R right join Portfolio as P ON R.portIdx = P.portIdx
            WHERE P.userIdx = :userIdx;
"""

    //portIdx로 userIdx 가져오기
    public static final String GET_USER_IDX_BY_PORT ="""
        SELECT userIdx from Portfolio where portIdx = :portIdx
    """;

    //portIdx로 accountIdx 가져오기
    public static final String GET_ACCOUNT_IDX_BY_PORT ="""
        SELECT accountIdx from Portfolio where portIdx = :portIdx
    """;

    //accountIdx로 marketIdx 가져오기
    public static final String GET_MARKET_IDX_BY_ACCOUNT ="""
        SELECT marketIdx from Account where accountIdx = :accountIdx
    """;

    //representIdx로 portIdx 가져오기
    public static final String GET_PORT_IDX_BY_REPRESENT ="""
        SELECT portIdx from Represent where representIdx = :representIdx
    """;

    //portIdx로 status 가져오기
    public static final String GET_STATUS_BY_PORT ="""
        SELECT status from Portfolio where portIdx = :portIdx
    """;

    public static final String FIND_ALL_REPRESENT_BY_PORT = """
			SELECT r.representIdx, r.portIdx, r.coinIdx FROM Represent r 
			JOIN Coin c on c.coinIdx = r.coinIdx WHERE portIdx = :portIdx AND r.status = 'active'
	"""

}
