package com.bit.kodari.repository.portfolio;

class PortfolioSql {
    //포트폴리오 등록
    public static final String INSERT = """
			INSERT INTO Portfolio (userIdx, accountIdx)
			values (:userIdx, :accountIdx)
	"""

    //포트폴리오 삭제
    public static final String DELETE = """
			UPDATE Portfolio SET status = 'inactive' WHERE portIdx = :portIdx
    """
}
