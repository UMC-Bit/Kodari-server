package com.bit.kodari.repository.Represent

class RepresentSql {
    //대표 코인 등록
    public static final String INSERT = """
			INSERT INTO Represent (portIdx, coinIdx) values (:portIdx, :coinIdx)
	"""

    //대표 코인 조회
    public static final String FIND_USER_REPRESENT = """
			SELECT representIdx, portIdx, coinIdx, status FROM Represent WHERE portIdx = :portIdx AND status = 'active'
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
