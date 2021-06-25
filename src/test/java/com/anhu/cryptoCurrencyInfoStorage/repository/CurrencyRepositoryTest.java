package com.anhu.cryptoCurrencyInfoStorage.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.anhu.cryptoCurrencyInfoStorage.StandardData;
import com.anhu.cryptoCurrencyInfoStorage.model.Currency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository repository;

    @Test
    public void testStartup_shouldHave4Entries() {
        Iterable<Currency> currencies = repository.findAll();
        assertThat(currencies).hasSize(4);
    }

    @Test
    public void testAddCurrency_repositoryContainsCurrency() {
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
    public void testFindAllCurrencies_shouldFindAllEntries(){
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
    public void testDeleteAllCurrencies_repositoryShouldBeEmpty() {
        repository.deleteAll();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void testSaveAfterDelete_canSaveInEmptyRepository() {
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
    public void testDeleteEntryById_canDeleteEntryByIdAtAnyPosition() {
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
    public void testUpdateEntry_shouldUpdateCurrencyById() {
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
            currency.setNumberOfCoins(updatedCurrency.getNumberOfCoins());
            currency.setMarketCap(updatedCurrency.getMarketCap());

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
    public void testUpdateEntry_updateShouldNotAffectOtherEntries() {

        int indexCurrencyToBeChanged = 0;
        String ticker = StandardData.getStandardTickers()[indexCurrencyToBeChanged];
        String name = "updated " + StandardData.getStandardNames()[indexCurrencyToBeChanged];
        long number_of_coins = StandardData.getStandardNrOfCoins()[indexCurrencyToBeChanged] + 10;
        long market_cap = StandardData.getStandardMarketCap()[indexCurrencyToBeChanged] + 10;

        Currency updatedCurrency = new Currency(ticker, name, number_of_coins, market_cap);

        Optional<Currency> optionalCurrency = repository.findById(StandardData.getStandardTickers()[indexCurrencyToBeChanged]);
        if(optionalCurrency.isPresent()) {
            Currency currency = optionalCurrency.get();currency.setName(updatedCurrency.getName());
            currency.setNumberOfCoins(updatedCurrency.getNumberOfCoins());
            currency.setMarketCap(updatedCurrency.getMarketCap());

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

    @Test
    public void testPaging_onePage_shouldFindCorrectNumberOfPages(){
        int page = 0;
        int size = 4; // there are 4 entries so should give 1 page
        Pageable pagingSort = PageRequest.of(page, size);

        Page<Currency> pageTuts = repository.findAll(pagingSort);
        assertThat(pageTuts.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void testPaging_MultiplePages_shouldFindCorrectNumberOfPages(){
        int page = 0;
        int size = 3; // there are 4 entries so should give 2 pages
        Pageable paging = PageRequest.of(page, size);

        Page<Currency> pageTuts = repository.findAll(paging);
        assertThat(pageTuts.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void testPaging_MultiplePages_firstPageIsFullPage(){
        int page = 0;
        int size = 3; // there are 4 entries so should give 2 pages
        Pageable pagingSort = PageRequest.of(page, size);

        Page<Currency> pageTuts = repository.findAll(pagingSort);
        assertThat(pageTuts.getContent().size()).isEqualTo(size);
    }

    @Test
    public void testPaging_MultiplePages_LastPageHasSingleEntry(){
        int page = 1;
        int size = 3; // there are 4 entries so should give 2 pages
        Pageable paging = PageRequest.of(page, size);

        Page<Currency> pageTuts = repository.findAll(paging);
        assertThat(pageTuts.getContent().size()).isEqualTo(1);
    }

    @Test
    public void testOrderingAscending_orderOnNumberOfCoins_allEntriesOrderedByNumberOfCoins(){
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "numberOfCoins");
        orders.add(order);

        List<Currency> currencies = repository.findAll(Sort.by(orders));
        long previousEntryNumberOfCoins = 0;
        for(Currency currency : currencies){
            assertTrue(currency.getNumberOfCoins() >= previousEntryNumberOfCoins);
            previousEntryNumberOfCoins = currency.getNumberOfCoins();
        }

        System.out.println(currencies);
    }

    @Test
    public void testOrderingDescending_orderOnNumberOfCoins_allEntriesOrderedByNumberOfCoins(){
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "numberOfCoins");
        orders.add(order);

        List<Currency> currencies = repository.findAll(Sort.by(orders));
        long previousEntryNumberOfCoins = Long.MAX_VALUE;
        for(Currency currency : currencies){
            assertTrue(currency.getNumberOfCoins() <= previousEntryNumberOfCoins);
            previousEntryNumberOfCoins = currency.getNumberOfCoins();
        }

        System.out.println(currencies);
    }

    @Test
    public void testPagingWithOrdering_MultiplePages_PagesContainsItemsInOrder(){
        int page = 0;
        int size = 3; // there are 4 entries so should give 2 pages
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "numberOfCoins"));
        Pageable pagingSort0 = PageRequest.of(page, size, Sort.by(orders));
        Pageable pagingSort1 = PageRequest.of(page+1, size, Sort.by(orders));

        Page<Currency> pageTuts0 = repository.findAll(pagingSort0);
        Page<Currency> pageTuts1 = repository.findAll(pagingSort1);

        long previousEntryNumberOfCoins = 0;
        for(Currency currency : pageTuts0.getContent()){
            assertTrue(currency.getNumberOfCoins() >= previousEntryNumberOfCoins);
            previousEntryNumberOfCoins = currency.getNumberOfCoins();
        }
        assertTrue(pageTuts1.getContent().get(0).getNumberOfCoins() >= previousEntryNumberOfCoins);
    }


}
