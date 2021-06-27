package com.anhu.cryptoCurrencyInfoStorage.model;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyTest {

    @Test
    public void testConstructor(){
        //given
        String ticker = "DOGE";
        String name = "Dogecoin";
        long numberOfCoins = 129400000;
        long marketCap = 5310000;
        //when
        Currency currency = new Currency.Builder()
                .ticker(ticker)
                .name(name)
                .numberOfCoins(numberOfCoins)
                .marketCap(marketCap)
                .build();
        //then
        assertEquals(ticker, currency.getTicker());
        assertEquals(name, currency.getName());
        assertEquals(numberOfCoins, currency.getNumberOfCoins());
        assertEquals(marketCap, currency.getMarketCap());
    }
}
