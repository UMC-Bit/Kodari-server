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

    //portIdx로 userIdx 가져오기
    public static final String GET_USER_IDX_BY_PORT ="""
        SELECT userIdx from Portfolio where portIdx = :portIdx
    """;

    //representIdx로 portIdx 가져오기
    public static final String GET_PORT_IDX_BY_REPRESENT ="""
        SELECT portIdx from Represent where representIdx = :representIdx
    """;

    //portIdx로 status 가져오기
    public static final String GET_STATUS_BY_PORT ="""
        SELECT status from Portfolio where portIdx = :portIdx
    """;

}
