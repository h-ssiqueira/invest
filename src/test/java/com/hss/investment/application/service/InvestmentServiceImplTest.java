package com.hss.investment.application.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.persistence.InvestmentRepository;
import com.hss.investment.application.service.calculation.InvestmentCalculationDelegator;
import com.hss.openapi.model.SimulationInvestmentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentList;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentQueryDTO;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRequestList;
import static com.hss.investment.util.InvestmentDTOsMock.getIpcaTimelineList;
import static com.hss.investment.util.InvestmentDTOsMock.getProfitReturnDTO;
import static com.hss.investment.util.InvestmentDTOsMock.getSelicTimelineList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceImplTest {

    @Mock
    private InvestmentRepository repository;

    @Mock
    private InvestmentCalculationDelegator<InvestmentCalculationBase> delegator;

    @Mock
    private RateServiceImpl rateService;

    @InjectMocks
    private InvestmentServiceImpl service;

    @Test
    void addInvestments() {
        var response = service.addInvestments(getInvestmentRequestList());

        assertAll(
            () -> assertThat(response.getItems(), hasSize(2)),
            () -> verify(repository).saveAll(any()),
            () -> verifyNoInteractions(delegator, rateService)
        );
    }

    @Test
    void shouldRetrieveInvestments() {
        when(repository.findByParameters(any(), any())).thenReturn(getInvestmentList());
        when(rateService.getIpcaTimeline(any())).thenReturn(getIpcaTimelineList());
        when(rateService.getSelicTimeline(any())).thenReturn(getSelicTimelineList());
        when(delegator.delegate(any())).thenReturn(getProfitReturnDTO());

        var response = service.retrieveInvestments(getInvestmentQueryDTO());

        assertAll(
            () -> verify(repository).findByParameters(any(), any()),
            () -> verify(rateService, atMost(1)).getIpcaTimeline(any()),
            () -> verify(rateService, atMost(1)).getSelicTimeline(any()),
            () -> verify(delegator, times(3)).delegate(any())
        );
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @ParameterizedTest
    @MethodSource("com.hss.investment.util.InvestmentDTOsMock#getSimulationInvestmentRequestArgs")
    void shouldSimulateInvestments(SimulationInvestmentRequest dto) {
        when(delegator.delegate(any())).thenReturn(getProfitReturnDTO());
        when(rateService.getIpcaTimeline(any())).thenReturn(getIpcaTimelineList());
        when(rateService.getSelicTimeline(any())).thenReturn(getSelicTimelineList());

        var response = service.simulateInvestment(dto);

        assertAll(
            () -> verifyNoInteractions(repository),
            () -> verify(rateService, atMost(1)).getIpcaTimeline(any()),
            () -> verify(rateService, atMost(1)).getSelicTimeline(any()),
            () -> verify(delegator).delegate(any())
        );
    }
}