package com.anhu.cryptoCurrencyInfoStorage.controller;

import com.anhu.cryptoCurrencyInfoStorage.StandardData;
import com.anhu.cryptoCurrencyInfoStorage.model.Currency;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyControllerTestWithChanges {

    @Autowired
    private MockMvc mockMvc;

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

}
