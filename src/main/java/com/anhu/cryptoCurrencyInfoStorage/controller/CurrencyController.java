package com.anhu.cryptoCurrencyInfoStorage.controller;

import com.anhu.cryptoCurrencyInfoStorage.model.Currency;
import com.anhu.cryptoCurrencyInfoStorage.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    @Autowired
    CurrencyRepository currencyRepository;

    private static final Logger log = LoggerFactory.getLogger(CurrencyController.class);

    /**
     *
     * @param ticker
     * @return
     */
    @GetMapping("/currencies/{ticker}")
    public ResponseEntity<Currency> getCurrencyByTicker(@PathVariable("ticker") String ticker) {
        log.info("Get: /currencies/" + ticker);

        Optional<Currency> currencyData = currencyRepository.findById(ticker);

        if (currencyData.isPresent()) {
            log.info("HttpStatus.OK, returned " + currencyData.get());
            return new ResponseEntity<>(currencyData.get(), HttpStatus.OK);
        } else {
            log.info("HttpStatus.NOT_FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param currency
     * @return
     */
    @PostMapping("/currencies")
    public ResponseEntity<Currency> createCurrency(@RequestBody Currency currency) {
        log.info("Post: /currencies :" + currency);
        Optional<Currency> currencyData = currencyRepository.findById(currency.getTicker());
        if(currencyData.isPresent()){
            log.info("HttpStatus.CONFLICT");
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        try {
            Currency newCurrency = currencyRepository.save(
                    new Currency(currency.getTicker(), currency.getName(), currency.getNumberOfCoins(), currency.getMarketCap()));
            log.info("HttpStatus.CREATED, returned " + newCurrency);
            return new ResponseEntity<>(newCurrency, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("HttpStatus.INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/currencies/{ticker}")
    public ResponseEntity<Currency> updateCurrency(@PathVariable("ticker") String ticker, @RequestBody Currency currency) {
        log.info("Put: /currencies/" + ticker + " :" + currency);
        Optional<Currency> currencyData = currencyRepository.findById(ticker);

        if (currencyData.isPresent()) {
            Currency currencyToBeUpdated = currencyData.get();
            currencyToBeUpdated.setName(currency.getName());
            currencyToBeUpdated.setNumberOfCoins(currency.getNumberOfCoins());
            currencyToBeUpdated.setMarketCap(currency.getMarketCap());

            log.info("HttpStatus.OK, returned " + currencyToBeUpdated);
            return new ResponseEntity<>(currencyRepository.save(currencyToBeUpdated), HttpStatus.OK);
        } else {
            log.info("HttpStatus.NOT_FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/currencies/{ticker}")
    public ResponseEntity<HttpStatus> deleteCurrency(@PathVariable("ticker") String ticker) {
        log.info("Delete: /currencies/" + ticker);
        try {
            currencyRepository.deleteById(ticker);
            log.info("HttpStatus.NO_CONTENT");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.info("HttpStatus.INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/currencies")
    public ResponseEntity<Map<String, Object>> getAllCurrencies(
            @RequestParam(defaultValue = "-1") int page, // -1 to signal that no paging is asked
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "ticker") String[] sort,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        StringBuilder logText = new StringBuilder("Get: /currencies");
        if(page >= 0){
            logText.append(" page ").append(page).append(" size").append(size);
        }
        logText.append(" sort ").append(Arrays.toString(sort));
        logText.append(" sortDirection ").append(sortDirection);
        log.info(logText.toString());

        try {
            List<Sort.Order> orders = new ArrayList<>();
            Sort.Direction orderSortDirection = getSortDirection(sortDirection);
            for(String sortField : sort){
                orders.add(new Sort.Order(orderSortDirection, sortField));
            }

            List<Currency> currencies = new ArrayList<>();
            Map<String, Object> response = new HashMap<>();
            if(page >= 0){
                Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
                Page<Currency> pageOfCurrencies = currencyRepository.findAll(pagingSort);
                currencies.addAll(pageOfCurrencies.getContent());

                response.put("currencies", currencies);
                response.put("currentPage", pageOfCurrencies.getNumber());
                response.put("totalItems", pageOfCurrencies.getTotalElements());
                response.put("totalPages", pageOfCurrencies.getTotalPages());

            }else{
                currencies.addAll(currencyRepository.findAll(Sort.by(orders)));
                response.put("currencies", currencies);
            }

            if (currencies.isEmpty()) {
                log.info("HttpStatus.NO_CONTENT");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            log.info("HttpStatus.OK, returned " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);


        } catch (Exception e) {
            log.info("HttpStatus.INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }



}
