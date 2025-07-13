package com.hss.investment.endpoint;

import com.hss.investment.application.exception.ControllerExceptionAdvice;
import com.hss.investment.application.persistence.InvestmentRepository;
import com.hss.investment.application.service.RateKaggleUpdaterImpl;
import com.hss.investment.application.service.RateServiceImpl;
import com.hss.investment.config.IdempotencyInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.hss.investment.util.URLConstants.RATE_API_URL;
import static com.hss.investment.util.URLConstants.UPDATE_RATE_API_URL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class})
@AutoConfigureMockMvc
@SpringBootTest(classes = {RateController.class, ControllerExceptionAdvice.class})
class RateControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RateServiceImpl rateService;

    @MockitoBean
    private RateKaggleUpdaterImpl kaggleUpdater;

    @MockitoBean
    private InvestmentRepository repository;

    @MockitoBean
    private IdempotencyInterceptor idempotencyInterceptor;

    @Test
    void shouldRetrieveRates() throws Exception {
        mvc.perform(get(RATE_API_URL,"SELIC")
            .accept(APPLICATION_JSON)
            .queryParam("initialDate", "2020-03-13")
            .queryParam("finalDate", "2022-03-13")
        ).andExpect(status().isOk());
    }

    @Test
    void shouldValidateAcceptanceWhileRetrievingRates() throws Exception {
        mvc.perform(get(RATE_API_URL,"SELIC")
            .accept(APPLICATION_XML)
        ).andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldValidateMethodWhileRetrievingRates() throws Exception {
        mvc.perform(post(RATE_API_URL,"SELIC")
            .accept(APPLICATION_JSON)
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldValidateTypeWhileRetrievingRates() throws Exception {
        mvc.perform(get(RATE_API_URL,"RATE")
            .accept(APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateDateWhileRetrievingRates() throws Exception {
        mvc.perform(get(RATE_API_URL,"SELIC")
            .accept(APPLICATION_JSON)
            .queryParam("initialDate", "13-03-2020")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateRates() throws Exception {
        mvc.perform(post(UPDATE_RATE_API_URL)
        ).andExpect(status().isOk());
    }
}