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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyControllerTestWithoutChanges {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRetrieveById_idNotPresent_shouldGiveNotFoundStatusAndEmptyBody() throws Exception {
        //given
        String ticker = "nonExistentTicker";

        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies/" + ticker).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testRetrieveById_idPresent_shouldGiveOkStatusAndCurrencyBody() throws Exception {
        //given
        Currency currency = StandardData.getStandardCurrencies()[0];
        String ticker = currency.getTicker();

        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies/" + ticker).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(currency.toJson());
    }

    @Test
    public void testGetAll_noPagingNoSort_shouldGiveOkStatusAndAllEntries() throws Exception { //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        StringBuilder expectedContent = new StringBuilder("{\"currencies\":[");
        stream(StandardData.getStandardCurrencies()).sorted(Comparator.comparing(Currency::getTicker)).
                forEach(currency -> expectedContent.append(currency.toJson()).append(","));
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_noPagingSortOnMarketCap_shouldGiveOkStatusAndAllEntries() throws Exception {

        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?sort=marketCap").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        StringBuilder expectedContent = new StringBuilder("{\"currencies\":[");
        stream(StandardData.getStandardCurrencies()).sorted(Comparator.comparingLong(Currency::getMarketCap))
                .forEach(currency -> expectedContent.append(currency.toJson()).append(","));
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_noPagingSortOnMarketCapReverseOrder_shouldGiveOkStatusAndAllEntriesSorted() throws Exception {
        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?sort=marketCap&sortDirection=desc").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        StringBuilder expectedContent = new StringBuilder("{\"currencies\":[");
        stream(StandardData.getStandardCurrencies()).sorted(Comparator.comparingLong(Currency::getMarketCap).reversed())
                .forEach(currency -> expectedContent.append(currency.toJson()).append(","));
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_page1size4_shouldGiveNoContentStatusAndEmptyBody() throws Exception {
        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=1&size=4").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void testGetAll_firstPagePage0size3_shouldGiveOkStatusAndFirst3Entries() throws Exception {
        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=0&size=3").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        StringBuilder expectedContent = new StringBuilder("{\"totalItems\":4,\"totalPages\":2,\"currentPage\":0,\"currencies\":[");
        List<Currency> standardOrder = stream(StandardData.getStandardCurrencies()).
                sorted(Comparator.comparing(Currency::getTicker)).collect(Collectors.toList());
        for(int index = 0; index < 3; index++){
            expectedContent.append(standardOrder.get(index).toJson()).append(",");
        }
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_lastPagePage1size3_shouldGiveOkStatusAndLastEntry() throws Exception {
        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=1&size=3").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        StringBuilder expectedContent = new StringBuilder("{\"totalItems\":4,\"totalPages\":2,\"currentPage\":1,\"currencies\":[");
        List<Currency> standardOrder = stream(StandardData.getStandardCurrencies()).
                sorted(Comparator.comparing(Currency::getTicker)).collect(Collectors.toList());
        expectedContent.append(standardOrder.get(3).toJson());
        expectedContent.append("]}");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_firstPageWithSort_shouldGiveOkStatusAndFirst3EntriesSorted() throws Exception {
        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=0&size=3&sort=marketCap&sortDirection=desc")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        StringBuilder expectedContent = new StringBuilder("{\"totalItems\":4,\"totalPages\":2,\"currentPage\":0,\"currencies\":[");
        List<Currency> standardOrder = stream(StandardData.getStandardCurrencies()).
                sorted(Comparator.comparingLong(Currency::getMarketCap).reversed()).collect(Collectors.toList());
        for(int index = 0; index < 3; index++){
            expectedContent.append(standardOrder.get(index).toJson()).append(",");
        }
        expectedContent.deleteCharAt(expectedContent.lastIndexOf(","));
        expectedContent.append("]}");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }

    @Test
    public void testGetAll_lastPageWithSort_shouldGiveOkStatusAndLastEntry() throws Exception {
        //when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/currencies?page=1&size=3&sort=marketCap&sortDirection=desc")
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        StringBuilder expectedContent = new StringBuilder("{\"totalItems\":4,\"totalPages\":2,\"currentPage\":1,\"currencies\":[");
        List<Currency> standardOrder = stream(StandardData.getStandardCurrencies()).
                sorted(Comparator.comparingLong(Currency::getMarketCap).reversed()).collect(Collectors.toList());
        expectedContent.append(standardOrder.get(3).toJson());
        expectedContent.append("]}");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedContent.toString());
    }
}
