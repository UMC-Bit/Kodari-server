package com.bit.kodari.repository.user

class UserSql {
    public static final String INSERT = """
			INSERT INTO user (name, nickName, profileImgUrl, email, password, phoneNumber, deleteYN)
			values (:name, :nickName, :profileImgUrl, :email, :password, :phoneNumber, :deleteYN)
			""";
    public static final String SELECT = """
			SELECT * from user where userId = :userId
			""";

    public static final String UPDATE = """
			UPDATE user SET nickName = :nickName WHERE userId = :userId
""";
    public static final String DELETE = """
			UPDATE user SET deleteYN = 'Y' WHERE userId = :userId
""";
    public static final String FIND_BY_EMAIL = """
			SELECT * from user where email = :email
"""
    public static final String GET_EMAIL ="""
			SELECT email from user where userId = :userId
"""
    public static final String FIND_BY_NICKNAME = """
			SELECT * from user where nickName = :nickName
"""

