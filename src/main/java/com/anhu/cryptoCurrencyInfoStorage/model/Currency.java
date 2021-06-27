package com.anhu.cryptoCurrencyInfoStorage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Currency is the entity used to store records in the CurrencyRepository.
 */
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

    public Currency() {
    }

    private Currency(Builder builder) {
        this.ticker = builder.ticker;
        this.name = builder.name;
        this.numberOfCoins = builder.numberOfCoins;
        this.marketCap = builder.marketCap;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public long getNumberOfCoins() {
        return numberOfCoins;
    }

    public long getMarketCap() {
        return marketCap;
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
                ", numberOfCoins=" + numberOfCoins +
                ", marketCap=" + marketCap +
                '}';
    }

    public String toJson() {
        return "{\"ticker\":\"" + ticker + "\",\"name\":\"" + name +
                "\",\"numberOfCoins\":" + numberOfCoins + ",\"marketCap\":" + marketCap + "}";
    }

    public static class Builder{

        private String ticker;
        private String name;
        private long numberOfCoins;
        private long marketCap;

        public Builder(){
        }

        public Builder ticker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder numberOfCoins(long numberOfCoins) {
            this.numberOfCoins = numberOfCoins;
            return this;
        }

        public Builder marketCap(long marketCap) {
            this.marketCap = marketCap;
            return this;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNumberOfCoins(long numberOfCoins) {
            this.numberOfCoins = numberOfCoins;
        }

        public void setMarketCap(long marketCap) {
            this.marketCap = marketCap;
        }

        public Currency build(){
            return new Currency(this);
        }
    }
}
