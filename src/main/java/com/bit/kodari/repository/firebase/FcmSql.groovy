package com.bit.kodari.repository.firebase

class FcmSql {

    //userIdx로 token 가져오기
    public static final String GET_USER_TOKEN ="""
        SELECT token from User where userIdx = :userIdx AND status = 'active'
    """;
}
