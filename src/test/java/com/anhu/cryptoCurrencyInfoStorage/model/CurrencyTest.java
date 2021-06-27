package com.anhu.cryptoCurrencyInfoStorage.model;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyTest {

    @Test
    public void testConstructor(){
        String ticker = "DOGE";
        String name = "Dogecoin";
        long number_of_coins = 129400000;
        long market_cap = 5310000;
        Currency currency = new Currency(ticker, name, number_of_coins, market_cap);
        assertEquals(ticker, currency.getTicker());
        assertEquals(name, currency.getName());
        assertEquals(number_of_coins, currency.getNumberOfCoins());
        assertEquals(market_cap, currency.getMarketCap());
    }
}
