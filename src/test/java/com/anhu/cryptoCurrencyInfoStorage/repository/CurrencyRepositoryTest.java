package com.anhu.cryptoCurrencyInfoStorage.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.anhu.cryptoCurrencyInfoStorage.StandardData;
import com.anhu.cryptoCurrencyInfoStorage.model.Currency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository repository;

    @Test
    public void shouldHave4EntriesAtStartup() {
        Iterable<Currency> currencies = repository.findAll();
        assertThat(currencies).hasSize(4);
    }

    @Test
    public void shouldStoreACurrency() {
        String ticker = "DOGE";
        String name = "Dogecoin";
        long number_of_coins = 129400000;
        long market_cap = 5310000;

        Currency currency = repository.save(new Currency(ticker, name, number_of_coins, market_cap));
        Iterable<Currency> currencies = repository.findAll();
        assertThat(currencies).hasSize(5);

        assertThat(currency).hasFieldOrPropertyWithValue("ticker", ticker);
        assertThat(currency).hasFieldOrPropertyWithValue("name", name);
        assertThat(currency).hasFieldOrPropertyWithValue("number_of_coins", number_of_coins);
        assertThat(currency).hasFieldOrPropertyWithValue("market_cap", market_cap);
    }

    @Test
    public void shouldFindAllEntries(){
        String ticker = "DOGE";
        String name = "Dogecoin";
        long number_of_coins = 129400000;
        long market_cap = 5310000;
        Currency newCurrency = new Currency(ticker, name, number_of_coins, market_cap);

        repository.save(newCurrency);
        Iterable<Currency> currencies = repository.findAll();
        assertThat(currencies).hasSize(5).contains(StandardData.getStandardCurrencies()).contains(newCurrency);
    }

    @Test
    public void shouldDeleteAllCurrencies() {
        repository.deleteAll();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void canStoreAfterDelete() {
        repository.deleteAll();
        assertThat(repository.findAll()).isEmpty();

        String ticker = "DOGE";
        String name = "Dogecoin";
        long number_of_coins = 129400000;
        long market_cap = 5310000;

        Currency currency = repository.save(new Currency(ticker, name, number_of_coins, market_cap));
        Iterable<Currency> currencies = repository.findAll();
        assertThat(currencies).hasSize(1).contains(currency);
    }

    @Test
    public void shouldDeleteCurrencyById() {
        Currency[] currencies = StandardData.getStandardCurrencies();

        // delete one from the middle
        repository.deleteById(StandardData.getStandardTickers()[2]);
        Iterable<Currency> repositoryCurrencies = repository.findAll();
        assertThat(repositoryCurrencies).hasSize(3).contains(currencies[0], currencies[1], currencies[3]);

        // delete one from the end
        repository.deleteById(StandardData.getStandardTickers()[3]);
        repositoryCurrencies = repository.findAll();
        assertThat(repositoryCurrencies).hasSize(2).contains(currencies[0], currencies[1]);

        // delete one from the start
        repository.deleteById(StandardData.getStandardTickers()[0]);
        repositoryCurrencies = repository.findAll();
        assertThat(repositoryCurrencies).hasSize(1).contains(currencies[1]);

        // delete the last entity in the table
        repository.deleteById(StandardData.getStandardTickers()[1]);
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void shouldUpdateCurrencyById() {
        int indexCurrencyToBeChanged = 0;
        String ticker = StandardData.getStandardTickers()[indexCurrencyToBeChanged];
        String name = "updated " + StandardData.getStandardNames()[indexCurrencyToBeChanged];
        long number_of_coins = StandardData.getStandardNrOfCoins()[indexCurrencyToBeChanged] + 10;
        long market_cap = StandardData.getStandardMarketCap()[indexCurrencyToBeChanged] + 10;

        Currency updatedCurrency = new Currency(ticker, name, number_of_coins, market_cap);

        Optional<Currency> optionalCurrency = repository.findById(StandardData.getStandardTickers()[indexCurrencyToBeChanged]);
        if(optionalCurrency.isPresent()) {
            Currency currency = optionalCurrency.get();
            currency.setName(updatedCurrency.getName());
            currency.setNumber_of_coins(updatedCurrency.getNumber_of_coins());
            currency.setMarket_cap(updatedCurrency.getMarket_cap());

            repository.save(currency);


            Optional<Currency> optionalCheckCurrency = repository.findById(StandardData.getStandardTickers()[indexCurrencyToBeChanged]);
            if(optionalCheckCurrency.isPresent()) {
                Currency checkCurrency = optionalCheckCurrency.get();
                assertThat(checkCurrency).hasFieldOrPropertyWithValue("ticker", ticker);
                assertThat(checkCurrency).hasFieldOrPropertyWithValue("name", name);
                assertThat(checkCurrency).hasFieldOrPropertyWithValue("number_of_coins", number_of_coins);
                assertThat(checkCurrency).hasFieldOrPropertyWithValue("market_cap", market_cap);
            }else {
                fail("did not find currency by id");
            }
        }else {
            fail("did not find currency by id");
        }
    }

    @Test
    public void updateShouldNotAffectOtherEntries() {

        int indexCurrencyToBeChanged = 0;
        String ticker = StandardData.getStandardTickers()[indexCurrencyToBeChanged];
        String name = "updated " + StandardData.getStandardNames()[indexCurrencyToBeChanged];
        long number_of_coins = StandardData.getStandardNrOfCoins()[indexCurrencyToBeChanged] + 10;
        long market_cap = StandardData.getStandardMarketCap()[indexCurrencyToBeChanged] + 10;

        Currency updatedCurrency = new Currency(ticker, name, number_of_coins, market_cap);

        Optional<Currency> optionalCurrency = repository.findById(StandardData.getStandardTickers()[indexCurrencyToBeChanged]);
        if(optionalCurrency.isPresent()) {
            Currency currency = optionalCurrency.get();currency.setName(updatedCurrency.getName());
            currency.setNumber_of_coins(updatedCurrency.getNumber_of_coins());
            currency.setMarket_cap(updatedCurrency.getMarket_cap());

            repository.save(currency);
            Iterable<Currency> repositoryCurrencies = repository.findAll();
            Currency[] currencies = StandardData.getStandardCurrencies();
            assertThat(repositoryCurrencies).hasSize(4).
                    contains(updatedCurrency, currencies[1], currencies[2], currencies[3]).
                    doesNotContain(currencies[0]);
        }else {
            fail("did not find currency by id");
        }
    }

}
