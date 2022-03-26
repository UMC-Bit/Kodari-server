package com.bit.kodari.repository.registerCoinAlarm;


import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RegisterCoinAlarmRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    RegisterCoinAlarmSql registerCoinAlarmSql;
    public RegisterCoinAlarmRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

}
