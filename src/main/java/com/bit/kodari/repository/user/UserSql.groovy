package com.bit.kodari.repository.user

 class UserSql {
    public static final String INSERT = """
			INSERT INTO User (nickName,email,password, profileImgUrl, authKey, status)
			values (:nickName, :email,  :password, :profileImgUrl, :authKey, :status)
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
			SELECT * 
			FROM User 
			WHERE email = :email AND status = "active"
"""


     public static final String FIND_BY_USERIDX = """
			SELECT * 
			FROM User 
			WHERE userIdx = :userIdx AND status = "active"
"""

     // 유저인덱스로 status 조회
     public static final String FIND_STATUS_BY_USERIDX = """
			SELECT status
			FROM User 
			WHERE userIdx = :userIdx
"""
    public static final String GET_EMAIL = """
			SELECT email from User where userId = :userId
"""
    public static final String FIND_BY_NICKNAME = """
			SELECT * 
			FROM User 
			WHERE nickName = :nickName AND status = "active"
"""
     public static final String FIND_USERS = """
			SELECT * 
			FROM User 
			WHERE status = "active"
"""
     public static final String FIND_LAST_INSERT_ID = """
			SELECT last_insert_id()
"""

}
