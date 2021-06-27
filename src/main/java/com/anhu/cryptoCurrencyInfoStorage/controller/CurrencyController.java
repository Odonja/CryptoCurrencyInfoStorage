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
     * Retrieves a specific record
     * @param ticker The ticker of the requested record
     * @return the record if present, HttpStatus.NOT_FOUND otherwise
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
     * Create a new record
     * @param newCurrency The record to be created
     * @return The record if not yet present or
     * HttpStatus.CONFLICT if there already exists a record for ticker currencyBuilder.ticker
     */
    @PostMapping("/currencies")
    public ResponseEntity<Currency> createCurrency(@RequestBody Currency.Builder newCurrency) {
        Currency currency = newCurrency.build();
        log.info("Post: /currencies :" + currency);
        Optional<Currency> currencyData = currencyRepository.findById(currency.getTicker());
        if(currencyData.isPresent()){
            log.info("HttpStatus.CONFLICT");
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        try {
            Currency savedCurrency = currencyRepository.save(currency);
            log.info("HttpStatus.CREATED, returned " + savedCurrency);
            return new ResponseEntity<>(savedCurrency, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("HttpStatus.INTERNAL_SERVER_ERROR");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a specific record
     * @param ticker The ticker of the record to be updated
     * @param currency The values of the new version of the record
     * @return The updated record if the database has a record for this ticker, HttpStatus.NOT_FOUND otherwise
     */
    @PutMapping("/currencies/{ticker}")
    public ResponseEntity<Currency> updateCurrency(@PathVariable("ticker") String ticker, @RequestBody Currency.Builder currency) {
        Currency currencyToBeUpdated = currency.build();
        log.info("Put: /currencies/" + ticker + " :" + currencyToBeUpdated);
        Optional<Currency> currencyData = currencyRepository.findById(ticker);

        if (currencyData.isPresent()) {
            log.info("HttpStatus.OK, returned " + currencyToBeUpdated);
            return new ResponseEntity<>(currencyRepository.save(currencyToBeUpdated), HttpStatus.OK);
        } else {
            log.info("HttpStatus.NOT_FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a specific record
     * @param ticker The ticker of the record to be deleted
     * @return HttpStatus.NO_CONTENT when the record is successfully deleted, HttpStatus.INTERNAL_SERVER_ERROR otherwise
     */
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

    /**
     * Retrieves a list of records
     * @param page The requested page of the list of records
     * @param size The size of the pages, default 3
     * @param sort The values of the record the record will be sorted by, default ticker
     * @param sortDirection The sorting direction of the sort, "desc" for descending, any other value for ascending
     * @return A map with the requested list of records and paging info if paging was requested
     */
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
            // retrieve make orders for sortiing
            List<Sort.Order> orders = new ArrayList<>();
            Sort.Direction orderSortDirection = getSortDirection(sortDirection);
            for(String sortField : sort){
                orders.add(new Sort.Order(orderSortDirection, sortField));
            }

            List<Currency> currencies = new ArrayList<>();
            Map<String, Object> response = new HashMap<>();
            if(page >= 0){ // paging is requested
                // get list of records for requested page
                Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));
                Page<Currency> pageOfCurrencies = currencyRepository.findAll(pagingSort);
                currencies.addAll(pageOfCurrencies.getContent());

                // add list of records and paging information to the response
                response.put("currencies", currencies);
                response.put("currentPage", pageOfCurrencies.getNumber());
                response.put("totalItems", pageOfCurrencies.getTotalElements());
                response.put("totalPages", pageOfCurrencies.getTotalPages());

            }else{ // no paging is requested
                currencies.addAll(currencyRepository.findAll(Sort.by(orders)));
                // add only list of records to the response
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
