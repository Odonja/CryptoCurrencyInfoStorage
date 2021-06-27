package com.anhu.cryptoCurrencyInfoStorage.controller;

import com.anhu.cryptoCurrencyInfoStorage.StandardData;
import com.anhu.cryptoCurrencyInfoStorage.model.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyControllerTestWithChanges {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreate_newCurrency_shouldGiveCreatedStatusAndCurrencyBody() throws Exception {
        //given
        String ticker = "DOGE";
        String name = "Dogecoin";
        long numberOfCoins = 129400000;
        long marketCap = 5310000;
        Currency newCurrency = new Currency.Builder()
                .ticker(ticker)
                .name(name)
                .numberOfCoins(numberOfCoins)
                .marketCap(marketCap)
                .build();

        //when
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/currencies").contentType(MediaType.APPLICATION_JSON).content(newCurrency.toJson())).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(newCurrency.toJson());
    }

    @Test
    public void testCreate_existingCurrency_shouldGiveConflictStatusAndEmptyBody() throws Exception {
        //given
        Currency newCurrency = StandardData.getStandardCurrencies()[0];

        //when
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/currencies").contentType(MediaType.APPLICATION_JSON).content(newCurrency.toJson())).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testUpdate_idPresent_shouldGiveOkStatusAndCurrencyBody() throws Exception {
        //given
        Currency oldCurrency = StandardData.getStandardCurrencies()[0];
        long newMarketCap = 123456000000L;

        Currency updatedCurrency = new Currency.Builder()
                .ticker(oldCurrency.getTicker())
                .name(oldCurrency.getName())
                .numberOfCoins(oldCurrency.getNumberOfCoins())
                .marketCap(newMarketCap)
                .build();
        String ticker = updatedCurrency.getTicker();

        //when
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/currencies/" + ticker).contentType(MediaType.APPLICATION_JSON).content(updatedCurrency.toJson())).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(updatedCurrency.toJson());
    }

    @Test
    public void testUpdate_idNotPresent_shouldGiveNotFoundStatusAndEmptyBody() throws Exception {
        //given
        String ticker = "DOGE";
        String name = "Dogecoin";
        long numberOfCoins = 129400000;
        long marketCap = 5310000;
        Currency newCurrency =new Currency.Builder()
                .ticker(ticker)
                .name(name)
                .numberOfCoins(numberOfCoins)
                .marketCap(marketCap)
                .build();

        //when
        MockHttpServletResponse response = mockMvc.perform(
                put("/api/currencies/" + ticker).contentType(MediaType.APPLICATION_JSON).content(newCurrency.toJson())).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testDelete_idPresent_shouldGiveNoContentStatusAndEmptyBody() throws Exception {
        //given
        Currency currency = StandardData.getStandardCurrencies()[3];

        //when
        MockHttpServletResponse response = mockMvc.perform(
                delete("/api/currencies/" + currency.getTicker())
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testDelete_idNotPresent_shouldGiveNotFoundStatusAndEmptyBody() throws Exception {
        //given
        String ticker = "nonExistingTicker";

        //when
        MockHttpServletResponse response = mockMvc.perform(
                delete("/api/currencies/" + ticker)
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

}
