package com.bit.kodari.repository.coin;

class CoinSql {
    //토론장 코인 조회
    public static final String LIST_COIN = """
         SELECT coinIdx, coinName, symbol, coinImg
         FROM Coin
         WHERE status = 'active'
         GROUP BY coinName
         """

    //토론장 코인이름 조회
    public static final String LIST_COIN_NAME = """
         SELECT  coinIdx, coinName, symbol, coinImg
         FROM Coin
         WHERE status = 'active' and coinName = :coinName
         GROUP BY coinName
         """
}
