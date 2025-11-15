package com.hss.investment.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hss.investment.application.exception.ControllerExceptionAdvice;
import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.IdempotencyRepository;
import com.hss.investment.application.persistence.InvestmentRepository;
import com.hss.investment.application.persistence.IpcaRepository;
import com.hss.investment.application.persistence.SelicRepository;
import com.hss.investment.application.service.InvestmentServiceImpl;
import com.hss.openapi.model.InvestmentResultResponseDTO;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.hss.investment.application.exception.ErrorMessages.INV_002;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRequestWrapper;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRequestWrapperError;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentSimulationResultResponseDTO;
import static com.hss.investment.util.InvestmentDTOsMock.getPartialInvestmentResultDataError;
import static com.hss.investment.util.InvestmentDTOsMock.getPartialInvestmentResultDataMultiStatus;
import static com.hss.investment.util.InvestmentDTOsMock.getPartialInvestmentResultDataSuccess;
import static com.hss.investment.util.InvestmentDTOsMock.getSimulationInvestmentRequest;
import static com.hss.investment.util.InvestmentDTOsMock.getSimulationInvestmentRequestError;
import static com.hss.investment.util.URLConstants.INVESTMENTS_API_URL;
import static com.hss.investment.util.URLConstants.INVESTMENTS_COMPLETE_API_URL;
import static com.hss.investment.util.URLConstants.INVESTMENTS_SIMULATION_API_URL;
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
@SpringBootTest(classes = {InvestmentController.class, ControllerExceptionAdvice.class})
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
        .disable(WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void shouldRetrieveEmptyInvestments() throws Exception {
        mvc.perform(get(INVESTMENTS_API_URL)
            .accept(APPLICATION_JSON)
            .queryParam("initialDate", "2020-03-13")
            .queryParam("finalDate", "2022-03-13")
            .queryParam("type", "LCA")
            .queryParam("aliquot", "POSTFIXED")
            .queryParam("sort", "investmentRange.finalDate,asc"))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldRetrieveInvestments() throws Exception {
        when(service.retrieveInvestments(any())).thenReturn(List.of(new InvestmentResultResponseDTO()));
        mvc.perform(get(INVESTMENTS_API_URL)
            .accept(APPLICATION_JSON)
            .queryParam("initialDate", "2020-03-13")
            .queryParam("finalDate", "2022-03-13")
            .queryParam("type", "LCA")
            .queryParam("aliquot", "POSTFIXED")
            .queryParam("sort", "investmentRange.finalDate,asc"))
            .andExpect(status().isOk());

        verify(service).retrieveInvestments(any());
    }

    @Test
    void shouldValidateAcceptanceWhileRetrievingInvestments() throws Exception {
        mvc.perform(get(INVESTMENTS_API_URL)
            .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldValidateMethodWhileRetrievingInvestments() throws Exception {
        mvc.perform(put(INVESTMENTS_API_URL)
            .accept(APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());
    }

    @ParameterizedTest
    @ValueSource(strings = {"type", "aliquot"})
    void shouldValidateTypeWhileRetrievingInvestments(String query) throws Exception {
        mvc.perform(get(INVESTMENTS_API_URL)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .queryParam(query, query))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateAcceptanceWhileCreatingInvestments() throws Exception {
        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(APPLICATION_JSON)
            .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldValidateContentTypeWhileCreatingInvestments() throws Exception {
        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(MediaType.APPLICATION_XML)
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldValidateErrorsWhileCreatingInvestments() throws Exception {
        when(service.addInvestments(any())).thenReturn(getPartialInvestmentResultDataError());

        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(getInvestmentRequestWrapperError()))
            .accept(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isBadRequest());

        verify(service).addInvestments(any());
    }

    @Test
    void shouldValidatePartialSuccessTypeWhileCreatingInvestments() throws Exception {
        when(service.addInvestments(any())).thenReturn(getPartialInvestmentResultDataMultiStatus());

        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(getInvestmentRequestWrapper()))
            .accept(APPLICATION_JSON))
            .andExpect(status().isMultiStatus());

        verify(service).addInvestments(any());
    }

    @Test
    void shouldCreateInvestments() throws Exception {
        when(service.addInvestments(any())).thenReturn(getPartialInvestmentResultDataSuccess());

        mvc.perform(post(INVESTMENTS_API_URL)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(getInvestmentRequestWrapper()))
            .accept(APPLICATION_JSON))
            .andExpect(status().isCreated());

        verify(service).addInvestments(any());
    }

    @Test
    void shouldValidateAcceptanceWhileSimulatingInvestments() throws Exception {
        mvc.perform(post(INVESTMENTS_SIMULATION_API_URL)
            .contentType(APPLICATION_JSON)
            .accept(MediaType.APPLICATION_XML))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldValidateContentTypeWhileSimulatingInvestments() throws Exception {
        mvc.perform(post(INVESTMENTS_SIMULATION_API_URL)
            .contentType(MediaType.APPLICATION_XML)
            .accept(APPLICATION_JSON))
            .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldValidateErrorsWhileSimulatingInvestments() throws Exception {
        when(service.simulateInvestment(any())).thenThrow(new InvestmentException(INV_002));

        mvc.perform(post(INVESTMENTS_SIMULATION_API_URL)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(getSimulationInvestmentRequestError()))
            .accept(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().isBadRequest());

        verify(service).simulateInvestment(any());
    }

    @Test
    void shouldSimulateInvestments() throws Exception {
        when(service.simulateInvestment(any())).thenReturn(getInvestmentSimulationResultResponseDTO());

        mvc.perform(post(INVESTMENTS_SIMULATION_API_URL)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(getSimulationInvestmentRequest()))
            .accept(APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).simulateInvestment(any());
    }

    @Test
    void shouldCompleteInvestment() throws Exception {
        mvc.perform(post(INVESTMENTS_COMPLETE_API_URL, UUID.randomUUID()))
            .andExpect(status().isOk());
    }

    @Test
    void shouldValidateMethodWhileCompletingInvestment() throws Exception {
        mvc.perform(put(INVESTMENTS_COMPLETE_API_URL, UUID.randomUUID()))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldValidateUUIDWhileCompletingInvestment() throws Exception {
        mvc.perform(post(INVESTMENTS_COMPLETE_API_URL, "UUID.randomUUID()"))
            .andExpect(status().isBadRequest());
    }
}