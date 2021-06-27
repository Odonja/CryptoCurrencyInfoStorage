package com.anhu.cryptoCurrencyInfoStorage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @Column(name = "ticker")
    private String ticker;

    @Column(name = "name")
    private String name;

    @Column(name = "number_of_coins")
    private long numberOfCoins;

    @Column(name = "market_cap")
    private long marketCap;

    public Currency(String ticker, String name, long numberOfCoins, long market_cap) {
        this.ticker = ticker;
        this.name = name;
        this.numberOfCoins = numberOfCoins;
        this.marketCap = market_cap;
    }

    public Currency() {

    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumberOfCoins() {
        return numberOfCoins;
    }

    public void setNumberOfCoins(long number_of_coins) {
        this.numberOfCoins = number_of_coins;
    }

    public long getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(long market_cap) {
        this.marketCap = market_cap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return numberOfCoins == currency.numberOfCoins && marketCap == currency.marketCap && ticker.equals(currency.ticker) && name.equals(currency.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, name, numberOfCoins, marketCap);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", number_of_coins=" + numberOfCoins +
                ", market_cap=" + marketCap +
                '}';
    }

    public String toJson() {
        return "{\"ticker\":\"" + ticker + "\",\"name\":\"" + name +
                "\",\"numberOfCoins\":" + numberOfCoins + ",\"marketCap\":" + marketCap + "}";
    }
}
