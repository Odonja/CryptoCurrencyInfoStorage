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
    private long number_of_coins;

    @Column(name = "market_cap")
    private long market_cap;


    public Currency(String ticker, String name, long number_of_coins, long market_cap) {
        this.ticker = ticker;
        this.name = name;
        this.number_of_coins = number_of_coins;
        this.market_cap = market_cap;
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

    public long getNumber_of_coins() {
        return number_of_coins;
    }

    public void setNumber_of_coins(long number_of_coins) {
        this.number_of_coins = number_of_coins;
    }

    public long getMarket_cap() {
        return market_cap;
    }

    public void setMarket_cap(long market_cap) {
        this.market_cap = market_cap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return number_of_coins == currency.number_of_coins && market_cap == currency.market_cap && ticker.equals(currency.ticker) && name.equals(currency.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, name, number_of_coins, market_cap);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", number_of_coins=" + number_of_coins +
                ", market_cap=" + market_cap +
                '}';
    }
}
