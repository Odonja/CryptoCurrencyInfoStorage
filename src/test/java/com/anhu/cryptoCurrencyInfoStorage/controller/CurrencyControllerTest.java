package com.anhu.cryptoCurrencyInfoStorage.controller;

import com.anhu.cryptoCurrencyInfoStorage.StandardData;
import com.anhu.cryptoCurrencyInfoStorage.model.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testRetrieveById_idNotPresent_shouldGiveNotFoundStatusAndEmptyBody() throws Exception {
        String ticker = "nonExistentTicker";
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies/" + ticker).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testRetrieveById_idPresent_shouldGiveOkStatusAndCurrencyBody() throws Exception {
        Currency currency = StandardData.getStandardCurrencies()[0];
        String ticker = currency.getTicker();
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies/" + ticker).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(currency.toJson());
    }

    @Test
    public void testCreate_newCurrency_shouldGiveCreatedStatusAndCurrencyBody() throws Exception {
        String ticker = "DOGE";
        String name = "Dogecoin";
        long number_of_coins = 129400000;
        long market_cap = 5310000;
        Currency newCurrency = new Currency(ticker, name, number_of_coins, market_cap);

        MockHttpServletResponse response = mockMvc.perform(
                post("/api/currencies").contentType(MediaType.APPLICATION_JSON).content(newCurrency.toJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(newCurrency.toJson());
    }

    @Test
    public void testCreate_existingCurrency_shouldGiveConflictStatusAndEmptyBody() throws Exception {
        Currency newCurrency = StandardData.getStandardCurrencies()[0];

        MockHttpServletResponse response = mockMvc.perform(
                post("/api/currencies").contentType(MediaType.APPLICATION_JSON).content(newCurrency.toJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testUpdate_idPresent_shouldGiveOkStatusAndCurrencyBody() throws Exception {
        Currency updatedCurrency = StandardData.getStandardCurrencies()[0];
        long newMarketCap = 123456000000L;
        updatedCurrency.setMarketCap(newMarketCap);
        String ticker = updatedCurrency.getTicker();
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/currencies/" + ticker).contentType(MediaType.APPLICATION_JSON).content(updatedCurrency.toJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(updatedCurrency.toJson());
    }

    @Test
    public void testUpdate_idNotPresent_shouldGiveNotFoundStatusAndEmptyBody() throws Exception {
        String ticker = "DOGE";
        String name = "Dogecoin";
        long number_of_coins = 129400000;
        long market_cap = 5310000;
        Currency newCurrency = new Currency(ticker, name, number_of_coins, market_cap);

        MockHttpServletResponse response = mockMvc.perform(
                put("/api/currencies/" + ticker).contentType(MediaType.APPLICATION_JSON).content(newCurrency.toJson())).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testGetAll_noPagingNoSort_shouldGiveOkStatusAndAllEntries() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        StringBuilder expectedContent = new StringBuilder("{\"currencies\":[");
        stream(StandardData.getStandardCurrencies()).sorted(Comparator.comparing(Currency::getTicker)).
                forEach(currency -> expectedContent.append(currency.toJson()).append(","));
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_noPagingSortOnMarketCap_shouldGiveOkStatusAndAllEntries() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?sort=marketCap").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        StringBuilder expectedContent = new StringBuilder("{\"currencies\":[");
        stream(StandardData.getStandardCurrencies()).sorted(Comparator.comparingLong(Currency::getMarketCap))
                .forEach(currency -> expectedContent.append(currency.toJson()).append(","));
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_noPagingSortOnMarketCapReverseOrder_shouldGiveOkStatusAndAllEntriesSorted() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?sort=marketCap&sortDirection=desc").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        StringBuilder expectedContent = new StringBuilder("{\"currencies\":[");
        stream(StandardData.getStandardCurrencies()).sorted(Comparator.comparingLong(Currency::getMarketCap).reversed())
                .forEach(currency -> expectedContent.append(currency.toJson()).append(","));
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_page1size4_shouldGiveNoContentStatusAndEmptyBody() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=1&size=4").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testGetAll_firstPagePage0size3_shouldGiveOkStatusAndFirst3Entries() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=0&size=3").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        StringBuilder expectedContent = new StringBuilder("{\"totalItems\":4,\"totalPages\":2,\"currentPage\":0,\"currencies\":[");
        List<Currency> standardOrder = stream(StandardData.getStandardCurrencies()).
                sorted(Comparator.comparing(Currency::getTicker)).collect(Collectors.toList());
        for(int index = 0; index < 3; index++){
            expectedContent.append(standardOrder.get(index).toJson()).append(",");
        }
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_lastPagePage1size3_shouldGiveOkStatusAndLastEntry() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=1&size=3").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        StringBuilder expectedContent = new StringBuilder("{\"totalItems\":4,\"totalPages\":2,\"currentPage\":1,\"currencies\":[");
        List<Currency> standardOrder = stream(StandardData.getStandardCurrencies()).
                sorted(Comparator.comparing(Currency::getTicker)).collect(Collectors.toList());
        expectedContent.append(standardOrder.get(3).toJson());
        expectedContent.append("]}");
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_firstPageWithSort_shouldGiveOkStatusAndFirst3EntriesSorted() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=0&size=3&sort=marketCap&sortDirection=desc")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        StringBuilder expectedContent = new StringBuilder("{\"totalItems\":4,\"totalPages\":2,\"currentPage\":0,\"currencies\":[");
        List<Currency> standardOrder = stream(StandardData.getStandardCurrencies()).
                sorted(Comparator.comparingLong(Currency::getMarketCap).reversed()).collect(Collectors.toList());
        for(int index = 0; index < 3; index++){
            expectedContent.append(standardOrder.get(index).toJson()).append(",");
        }
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_lastPageWithSort_shouldGiveOkStatusAndLastEntry() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=1&size=3&sort=marketCap&sortDirection=desc")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        StringBuilder expectedContent = new StringBuilder("{\"totalItems\":4,\"totalPages\":2,\"currentPage\":1,\"currencies\":[");
        List<Currency> standardOrder = stream(StandardData.getStandardCurrencies()).
                sorted(Comparator.comparingLong(Currency::getMarketCap).reversed()).collect(Collectors.toList());
        expectedContent.append(standardOrder.get(3).toJson());
        expectedContent.append("]}");
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

}
