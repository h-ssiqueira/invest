package com.hss.investment.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hss.investment.application.exception.ControllerExceptionAdvice;
import com.hss.investment.application.persistence.IdempotencyRepository;
import com.hss.investment.application.persistence.InvestmentRepository;
import com.hss.investment.application.persistence.IpcaRepository;
import com.hss.investment.application.persistence.SelicRepository;
import com.hss.investment.application.service.InvestmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRequestWrapper;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRequestWrapperError;
import static com.hss.investment.util.InvestmentDTOsMock.getPartialInvestmentResultDataError;
import static com.hss.investment.util.InvestmentDTOsMock.getPartialInvestmentResultDataMultiStatus;
import static com.hss.investment.util.InvestmentDTOsMock.getPartialInvestmentResultDataSuccess;
import static com.hss.investment.util.URLConstants.INVESTMENTS_API_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class})
@AutoConfigureMockMvc
@Import(ControllerExceptionAdvice.class)
@SpringBootTest(classes = InvestmentController.class)
class InvestmentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private InvestmentServiceImpl service;

    @MockitoBean
    private InvestmentRepository investmentRepository;

    @MockitoBean
    private SelicRepository selicRepository;

    @MockitoBean
    private IpcaRepository ipcaRepository;

    @MockitoBean
    private IdempotencyRepository idempotencyRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void shouldRetrieveInvestments() throws Exception {
        mvc.perform(get(INVESTMENTS_API_URL)
            .accept(APPLICATION_JSON)
            .queryParam("initialDate", "2020-03-13")
            .queryParam("finalDate", "2022-03-13")
            .queryParam("type", "LCA")
            .queryParam("aliquot", "POSTFIXED")
            .queryParam("sort", "finalDate,asc")
        ).andExpect(status().isOk());
    }

    @Test
    void shouldValidateAcceptanceWhileRetrievingInvestments() throws Exception {
        mvc.perform(get(INVESTMENTS_API_URL)
            .accept(MediaType.APPLICATION_XML)
        ).andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldValidateMethodWhileRetrievingInvestments() throws Exception {
        mvc.perform(put(INVESTMENTS_API_URL)
            .accept(APPLICATION_JSON)
        ).andExpect(status().isMethodNotAllowed());
    }

    @ParameterizedTest
    @ValueSource(strings = {"type", "aliquot"})
    void shouldValidateTypeWhileRetrievingInvestments(String query) throws Exception {
        mvc.perform(get(INVESTMENTS_API_URL)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .queryParam(query, query)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateAcceptanceWhileCreatingInvestments() throws Exception {
        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(APPLICATION_JSON)
            .accept(MediaType.APPLICATION_XML)
        ).andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldValidateContentTypeWhileCreatingInvestments() throws Exception {
        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(MediaType.APPLICATION_XML)
            .accept(APPLICATION_JSON)
        ).andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldValidateErrorsWhileCreatingInvestments() throws Exception {
        when(service.addInvestments(any())).thenReturn(getPartialInvestmentResultDataError());

        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(getInvestmentRequestWrapperError()))
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
        ).andExpect(status().isBadRequest());

        verify(service).addInvestments(any());
    }

    @Test
    void shouldValidatePartialSuccessTypeWhileCreatingInvestments() throws Exception {
        when(service.addInvestments(any())).thenReturn(getPartialInvestmentResultDataMultiStatus());

        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(getInvestmentRequestWrapper()))
            .accept(APPLICATION_JSON)
        ).andExpect(status().isMultiStatus());

        verify(service).addInvestments(any());
    }

    @Test
    void shouldCreateInvestments() throws Exception {
        when(service.addInvestments(any())).thenReturn(getPartialInvestmentResultDataSuccess());

        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(getInvestmentRequestWrapper()))
            .accept(APPLICATION_JSON)
        ).andExpect(status().isCreated());

        verify(service).addInvestments(any());
    }
}