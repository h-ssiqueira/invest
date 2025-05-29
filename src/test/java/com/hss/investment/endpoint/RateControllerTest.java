package com.hss.investment.endpoint;

import com.hss.investment.application.persistence.InvestmentRepository;
import com.hss.investment.application.service.RateServiceImpl;
import com.hss.investment.config.IdempotencyInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class})
@AutoConfigureMockMvc
@SpringBootTest
class RateControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RateServiceImpl rateService;

    @MockitoBean
    private InvestmentRepository repository;

    @MockitoBean
    private IdempotencyInterceptor idempotencyInterceptor;

    @Test
    void shouldRetrieveRates() throws Exception {
        mvc.perform(get(URLConstants.RATE_API_URL,"SELIC")
            .accept(MediaType.APPLICATION_JSON)
            .queryParam("initialDate", "2020-03-13")
            .queryParam("finalDate", "2022-03-13")
        ).andExpect(status().isOk());
    }

    @Test
    void shouldValidateAcceptanceWhileRetrievingRates() throws Exception {
        mvc.perform(get(URLConstants.RATE_API_URL,"SELIC")
            .accept(MediaType.APPLICATION_XML)
        ).andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldValidateMethodWhileRetrievingRates() throws Exception {
        mvc.perform(post(URLConstants.RATE_API_URL,"SELIC")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldValidateTypeWhileRetrievingRates() throws Exception {
        mvc.perform(get(URLConstants.RATE_API_URL,"RATE")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateDateWhileRetrievingRates() throws Exception {
        mvc.perform(get(URLConstants.RATE_API_URL,"SELIC")
            .accept(MediaType.APPLICATION_JSON)
            .queryParam("initialDate", "13-03-2020")
        ).andExpect(status().isBadRequest());
    }
}