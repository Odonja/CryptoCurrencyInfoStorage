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
        //when
        Iterable<Currency> currencies = repository.findAll();
        //then
        assertThat(currencies).hasSize(4);
    }

    @Test
    public void testAddCurrency_repositoryContainsCurrency() {
        //given
        Currency currency = new Currency.Builder()
                .ticker("DOGE")
                .name("Dogecoin")
                .numberOfCoins(129400000)
                .marketCap(5310000)
                .build();

        //when
        Currency newCurrency = repository.save(currency);
        Iterable<Currency> currencies = repository.findAll();

        //then
        assertThat(currencies).hasSize(5);
        assertThat(currency).hasFieldOrPropertyWithValue("ticker", currency.getTicker());
        assertThat(currency).hasFieldOrPropertyWithValue("name", currency.getName());
        assertThat(currency).hasFieldOrPropertyWithValue("numberOfCoins", currency.getNumberOfCoins());
        assertThat(currency).hasFieldOrPropertyWithValue("marketCap", currency.getMarketCap());
    }

    @Test
    public void testFindAllCurrencies_shouldFindAllEntries(){
        //given
        Currency newCurrency = new Currency.Builder()
                .ticker("DOGE")
                .name("Dogecoin")
                .numberOfCoins(129400000)
                .marketCap(5310000)
                .build();
        //when
        repository.save(newCurrency);
        Iterable<Currency> currencies = repository.findAll();
        //then
        assertThat(currencies).hasSize(5).contains(StandardData.getStandardCurrencies()).contains(newCurrency);
    }

    @Test
    public void testDeleteAllCurrencies_repositoryShouldBeEmpty() {
        //when
        repository.deleteAll();
        //then
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void testSaveAfterDelete_canSaveInEmptyRepository() {
        //given
        Currency newCurrency = new Currency.Builder()
                .ticker("DOGE")
                .name("Dogecoin")
                .numberOfCoins(129400000)
                .marketCap(5310000)
                .build();
        //when
        repository.deleteAll();
        Currency savedCurrency = repository.save(newCurrency);
        Iterable<Currency> currencies = repository.findAll();
        //then
        assertThat(currencies).hasSize(1).contains(savedCurrency);
    }

    @Test
    public void testDeleteEntryById_canDeleteEntryByIdAtAnyPosition() {
        //given
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
        //given
        int indexCurrencyToBeChanged = 0;
        String ticker = StandardData.getStandardTickers()[indexCurrencyToBeChanged];
        String name = "updated " + StandardData.getStandardNames()[indexCurrencyToBeChanged];
        long numberOfCoins = StandardData.getStandardNrOfCoins()[indexCurrencyToBeChanged] + 10;
        long marketCap = StandardData.getStandardMarketCap()[indexCurrencyToBeChanged] + 10;

        Currency updatedCurrency = new Currency.Builder()
                .ticker(ticker)
                .name(name)
                .numberOfCoins(numberOfCoins)
                .marketCap(marketCap)
                .build();

        //when
        Optional<Currency> optionalCurrency = repository.findById(StandardData.getStandardTickers()[indexCurrencyToBeChanged]);
        if(optionalCurrency.isPresent()) {
            repository.save(updatedCurrency);
            Optional<Currency> optionalCheckCurrency = repository.findById(StandardData.getStandardTickers()[indexCurrencyToBeChanged]);
            if(optionalCheckCurrency.isPresent()) {
                //then
                Currency checkCurrency = optionalCheckCurrency.get();
                assertThat(checkCurrency).hasFieldOrPropertyWithValue("ticker", ticker);
                assertThat(checkCurrency).hasFieldOrPropertyWithValue("name", name);
                assertThat(checkCurrency).hasFieldOrPropertyWithValue("numberOfCoins", numberOfCoins);
                assertThat(checkCurrency).hasFieldOrPropertyWithValue("marketCap", marketCap);
            }else {
                //then
                fail("did not find currency by id");
            }
        }else {
            //then
            fail("did not find currency by id");
        }
    }

    @Test
    public void testUpdateEntry_updateShouldNotAffectOtherEntries() {
        //given
        Currency[] currencies = StandardData.getStandardCurrencies();
        int indexCurrencyToBeChanged = 0;
        String ticker = currencies[indexCurrencyToBeChanged].getTicker();
        String name = "updated " + currencies[indexCurrencyToBeChanged].getName();
        long numberOfCoins = currencies[indexCurrencyToBeChanged].getNumberOfCoins()+ 10;
        long marketCap = currencies[indexCurrencyToBeChanged].getMarketCap() + 10;

        Currency updatedCurrency = new Currency.Builder()
                .ticker(ticker)
                .name(name)
                .numberOfCoins(numberOfCoins)
                .marketCap(marketCap)
                .build();

        //when
        Optional<Currency> optionalCurrency = repository.findById(StandardData.getStandardTickers()[indexCurrencyToBeChanged]);
        if(optionalCurrency.isPresent()) {
            repository.save(updatedCurrency);
            Iterable<Currency> repositoryCurrencies = repository.findAll();
            //then
            assertThat(repositoryCurrencies).hasSize(4).
                    contains(updatedCurrency, currencies[1], currencies[2], currencies[3]).
                    doesNotContain(currencies[0]);
        }else {
            //then
            fail("did not find currency by id");
        }
    }

    @Test
    public void testPaging_onePage_shouldFindCorrectNumberOfPages(){
        //given
        int page = 0;
        int size = 4; // there are 4 entries so should give 1 page
        Pageable pagingSort = PageRequest.of(page, size);
        //when
        Page<Currency> pageTuts = repository.findAll(pagingSort);
        //then
        assertThat(pageTuts.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void testPaging_MultiplePages_shouldFindCorrectNumberOfPages(){
        //given
        int page = 0;
        int size = 3; // there are 4 entries so should give 2 pages
        Pageable paging = PageRequest.of(page, size);
        //when
        Page<Currency> pageTuts = repository.findAll(paging);
        //then
        assertThat(pageTuts.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void testPaging_MultiplePages_firstPageIsFullPage(){
        //given
        int page = 0;
        int size = 3; // there are 4 entries so should give 2 pages
        Pageable pagingSort = PageRequest.of(page, size);
        //when
        Page<Currency> pageTuts = repository.findAll(pagingSort);
        //then
        assertThat(pageTuts.getContent().size()).isEqualTo(size);
    }

    @Test
    public void testPaging_MultiplePages_LastPageHasSingleEntry(){
        //given
        int page = 1;
        int size = 3; // there are 4 entries so should give 2 pages
        Pageable paging = PageRequest.of(page, size);
        //when
        Page<Currency> pageTuts = repository.findAll(paging);
        //then
        assertThat(pageTuts.getContent().size()).isEqualTo(1);
    }

    @Test
    public void testOrderingAscending_orderOnNumberOfCoins_allEntriesOrderedByNumberOfCoins(){
        //given
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "numberOfCoins");
        orders.add(order);
        //when
        List<Currency> currencies = repository.findAll(Sort.by(orders));

        //then
        long previousEntryNumberOfCoins = 0;
        for(Currency currency : currencies){
            assertTrue(currency.getNumberOfCoins() >= previousEntryNumberOfCoins);
            previousEntryNumberOfCoins = currency.getNumberOfCoins();
        }
    }

    @Test
    public void testOrderingDescending_orderOnNumberOfCoins_allEntriesOrderedByNumberOfCoins(){
        //given
        List<Sort.Order> orders = new ArrayList<>();
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "numberOfCoins");
        orders.add(order);
        //when
        List<Currency> currencies = repository.findAll(Sort.by(orders));
        //then
        long previousEntryNumberOfCoins = Long.MAX_VALUE;
        for(Currency currency : currencies){
            assertTrue(currency.getNumberOfCoins() <= previousEntryNumberOfCoins);
            previousEntryNumberOfCoins = currency.getNumberOfCoins();
        }
    }

    @Test
    public void testPagingWithOrdering_MultiplePages_PagesContainsItemsInOrder(){
        //given
        int page = 0;
        int size = 3; // there are 4 entries so should give 2 pages
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "numberOfCoins"));
        Pageable pagingSort0 = PageRequest.of(page, size, Sort.by(orders));
        Pageable pagingSort1 = PageRequest.of(page+1, size, Sort.by(orders));

        //when
        Page<Currency> pageTuts0 = repository.findAll(pagingSort0);
        Page<Currency> pageTuts1 = repository.findAll(pagingSort1);

        //then
        long previousEntryNumberOfCoins = 0;
        for(Currency currency : pageTuts0.getContent()){
            assertTrue(currency.getNumberOfCoins() >= previousEntryNumberOfCoins);
            previousEntryNumberOfCoins = currency.getNumberOfCoins();
        }
        assertTrue(pageTuts1.getContent().get(0).getNumberOfCoins() >= previousEntryNumberOfCoins);
    }


}
