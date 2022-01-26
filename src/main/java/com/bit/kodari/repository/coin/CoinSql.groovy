package com.bit.kodari.repository.coin;

class CoinSql {
    //토론장 코인 조회
    public static final String LIST_COIN = """
         SELECT coinName, symbol, coinImg
         FROM Coin
         WHERE status = 'active'
         """
}
