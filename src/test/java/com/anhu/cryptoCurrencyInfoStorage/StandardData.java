package com.anhu.cryptoCurrencyInfoStorage;

import com.anhu.cryptoCurrencyInfoStorage.model.Currency;

public class StandardData {

    public static String[] getStandardTickers(){
        return new String[]{"BTC", "ETH", "XRP", "BCH"};
    }

    public static String[] getStandardNames(){
        return new String[]{"Bitcoin", "Ethereum", "Ripple", "BitcoinCash"};
    }

    public static long[] getStandardNrOfCoins(){
        return new long[]{16770000L, 96710000L, 38590000000L, 16670000L};
    }

    public static long[] getStandardMarketCap(){
        return new long[]{189580000000L, 69280000000L, 64750000000L, 69020000000L};
    }

    public static Currency[] getStandardCurrencies(){
        String[] tickers = getStandardTickers();
        String[] names = getStandardNames();
        long[] coins = getStandardNrOfCoins();
        long[] cap = getStandardMarketCap();

        Currency[] currencies = new Currency[4];
        for(int index = 0; index < currencies.length; index++){
            currencies[index] = new Currency(tickers[index], names[index], coins[index], cap[index]);
        }
        return currencies;
    }
}
