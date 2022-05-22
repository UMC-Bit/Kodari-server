package com.bit.kodari.repository.email

class EmailSql {
    public static final String GET_USER_INFO = """
			SELECT userIdx, nickName, email, authKey
			FROM User 
			WHERE userIdx = :userIdx
            """

    public static final String CHECK_AUTHKEY = """
			SELECT case when COUNT(ifnull(userIdx,0)) = 1 then 'true' when COUNT(ifnull(userIdx,0)) = 0 then 'false' end as 'check'
			FROM User
			WHERE email = :email and authKey = :authKey
            """

    public static final String UPDATE_AUTHKEY = """
			UPDATE User
            SET authKey = 'Y' WHERE authKey = :authKey
            """

    public static final String GET_AUTHKEY = """
        SELECT authKey
        FROM User
        WHERE email = :email
        """

}
