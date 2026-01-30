package com.hss.investment.application.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.ConfigurationDao;
import com.hss.investment.application.persistence.InvestmentRepository;
import com.hss.investment.application.persistence.entity.Investment;
import com.hss.investment.application.service.calculation.InvestmentCalculationDelegator;
import com.hss.openapi.model.SimulationInvestmentRequest;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static com.hss.investment.util.InvestmentDTOsMock.getFutureInvestment;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestment;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentList;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentQueryDTO;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRequestList;
import static com.hss.investment.util.InvestmentDTOsMock.getIpcaTimelineList;
import static com.hss.investment.util.InvestmentDTOsMock.getProfitReturnDTO;
import static com.hss.investment.util.InvestmentDTOsMock.getSelicTimelineList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
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

    @Mock
    private ConfigurationDao configurationDao;

    @InjectMocks
    private InvestmentServiceImpl service;

    @Test
    void addInvestments() {
        var response = service.addInvestments(getInvestmentRequestList());

        assertAll(
            () -> assertThat(response.getItems(), hasSize(2)),
            () -> verify(repository).saveAll(any()),
            () -> verifyNoInteractions(delegator, rateService, configurationDao)
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
            () -> assertThat(response.getContent(), hasSize(3)),
            () -> verify(repository).findByParameters(any(), any()),
            () -> verify(rateService, atMost(1)).getIpcaTimeline(any()),
            () -> verify(rateService, atMost(1)).getSelicTimeline(any()),
            () -> verify(delegator, times(3)).delegate(any()),
            () -> verifyNoInteractions(configurationDao)
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
            () -> assertThat(response, notNullValue()),
            () -> verifyNoInteractions(repository, configurationDao),
            () -> verify(rateService, atMost(1)).getIpcaTimeline(any()),
            () -> verify(rateService, atMost(1)).getSelicTimeline(any()),
            () -> verify(delegator).delegate(any())
        );
    }

    @ParameterizedTest
    @MethodSource("com.hss.investment.util.InvestmentDTOsMock#getCompleteInvestmentRequestArgs")
    void shouldValidateAllExceptionsWhenTryingToCompleteInvestmentsByAPI(Optional<Investment> inv, String exceptionMessage) {
        when(repository.findById(any())).thenReturn(inv);
        var id = UUID.randomUUID();

        assertThrowsExactly(InvestmentException.class, () -> service.completeInvestment(id), exceptionMessage);

        assertAll(
            () -> verify(repository).findById(any()),
            () -> verifyNoInteractions(delegator, rateService, configurationDao)
        );
    }

    @Test
    void shouldCompleteInvestmentByAPI() {
        when(repository.findById(any())).thenReturn(getFutureInvestment());
        when(delegator.delegate(any())).thenReturn(getProfitReturnDTO());
        var id = UUID.randomUUID();

        service.completeInvestment(id);

        assertAll(
            () -> verify(repository).findById(any()),
            () -> verify(delegator).delegate(any()),
            () -> verify(repository).save(any()),
            () -> verifyNoInteractions(rateService, configurationDao)
        );
    }

    @Test
    void shouldNotExecuteTwiceCompletedInvestmentsUpdate() {
        when(configurationDao.getLastInvestmentUpdated()).thenReturn(Optional.of(LocalDate.now()));

        service.updateInvestments();

        assertAll(
            () -> verify(configurationDao).getLastInvestmentUpdated(),
            () -> verifyNoInteractions(delegator, rateService, repository)
        );
    }

    @Test
    void shouldExecuteTwiceCompletedInvestmentsUpdate() {
        when(configurationDao.getLastInvestmentUpdated()).thenReturn(Optional.of(LocalDate.now().minusMonths(1)));
        when(repository.findByIncompleted(any())).thenReturn(new PageImpl<>(singletonList(getInvestment())), Page.empty());
        when(delegator.delegate(any())).thenReturn(getProfitReturnDTO());

        service.updateInvestments();

        assertAll(
            () -> verify(configurationDao).getLastInvestmentUpdated(),
            () -> verify(repository, times(2)).findByIncompleted(any()),
            () -> verify(delegator).delegate(any()),
            () -> verify(repository).save(any()),
            () -> verify(configurationDao).saveLastInvestmentUpdate(any()),
            () -> verifyNoInteractions(rateService)
        );
    }
}