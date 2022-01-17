package com.bit.kodari.repository.user

 class UserSql {
    public static final String INSERT = """
			INSERT INTO User (nickName,email,password, profileImgUrl, status)
			values (:nickName, :email,  :password, :profileImgUrl, :status)
			"""
    public static final String SELECT = """
			SELECT * from User where userId = :userId
			"""

    public static final String UPDATE = """
			UPDATE User SET nickName = :nickName WHERE userId = :userId
"""
     public static final String UPDATE_NICKNAME = """
			UPDATE User SET nickName = :nickName WHERE userIdx = :userIdx
"""

     public static final String UPDATE_PROFILEIMGURL = """
			UPDATE User SET profileImgUrl = :profileImgUrl WHERE userIdx = :userIdx
"""
     public static final String UPDATE_PASSWORD = """
			UPDATE User SET password = :password WHERE userIdx = :userIdx
"""
     public static final String DELETE = """
			UPDATE User SET status = "inactive" WHERE userIdx = :userIdx
"""
    public static final String FIND_BY_EMAIL = """
			SELECT * from User where email = :email
"""
    public static final String GET_EMAIL = """
			SELECT email from User where userId = :userId
"""
    public static final String FIND_BY_NICKNAME = """
			SELECT * from User where nickName = :nickName
"""
     public static final String FIND_USERS = """
			SELECT * from User
"""
     public static final String FIND_LAST_INSERT_ID = """
			SELECT last_insert_id()
"""

}
