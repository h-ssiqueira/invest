package com.hss.investment.application.service;

import com.hss.investment.application.persistence.IpcaRepository;
import com.hss.investment.application.persistence.SelicRepository;
import com.hss.investment.application.persistence.entity.Ipca;
import com.hss.investment.application.persistence.entity.Selic;
import com.hss.investment.application.service.mapper.GeneralMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRange;
import static com.hss.investment.util.InvestmentDTOsMock.getIpca;
import static com.hss.investment.util.InvestmentDTOsMock.getIpcaQueryDTO;
import static com.hss.investment.util.InvestmentDTOsMock.getRateQueryResultList;
import static com.hss.investment.util.InvestmentDTOsMock.getSelic;
import static com.hss.investment.util.InvestmentDTOsMock.getSelicQueryDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateServiceImplTest {

    @Spy
    private final GeneralMapper mapper = Mappers.getMapper(GeneralMapper.class);

    @Mock
    private SelicRepository selicRepository;

    @Mock
    private IpcaRepository ipcaRepository;

    @InjectMocks
    private RateServiceImpl service;

    @Test
    void shouldRetrieveSelicRates() {
        when(selicRepository.findByReferenceDateBetween(any(), any())).thenReturn(getRateQueryResultList());

        var response = service.retrieveRates(getSelicQueryDTO());

        assertAll(
            () -> assertThat(response, hasSize(1)),
            () -> assertThat(response, hasItem(allOf(
                hasProperty("rate", equalTo(1.0D)),
                hasProperty("initialDate", equalTo(LocalDate.of(2025,8,31))),
                hasProperty("finalDate", equalTo(null))))),
            () -> verify(selicRepository).findByReferenceDateBetween(any(), any()),
            () -> verify(mapper).toRateResponseWrapperDataItemsInner(any()),
            () -> verifyNoInteractions(ipcaRepository)
        );
    }

    @Test
    void shouldRetrieveIpcaRates() {
        when(ipcaRepository.findByReferenceDateBetween(any(), any())).thenReturn(getRateQueryResultList());

        var response = service.retrieveRates(getIpcaQueryDTO());

        assertAll(
            () -> assertThat(response, hasSize(1)),
            () -> assertThat(response, hasItem(allOf(
                hasProperty("rate", equalTo(1.0D)),
                hasProperty("initialDate", equalTo(LocalDate.of(2025,8,31))),
                hasProperty("finalDate", equalTo(null))))),
            () -> verify(ipcaRepository).findByReferenceDateBetween(any(), any()),
            () -> verify(mapper).toRateResponseWrapperDataItemsInner(any()),
            () -> verifyNoInteractions(selicRepository)
        );
    }

    @Test
    void shouldInsertNewIPCAList() {
        when(ipcaRepository.findFirstByOrderByReferenceDateDesc()).thenReturn(Optional.empty());

        service.processIpca(List.of(getIpca()));

        assertAll(
            () -> verify(ipcaRepository).findFirstByOrderByReferenceDateDesc(),
            () -> verify(ipcaRepository).saveAllAndFlush(any()),
            () -> verifyNoInteractions(selicRepository)
        );
    }

    @Test
    void shouldValidateExistingIPCAList() {
        var ipca = getIpca();
        when(ipcaRepository.findFirstByOrderByReferenceDateDesc()).thenReturn(Optional.of(ipca));
        var list = new ArrayList<Ipca>();
        list.add(ipca);

        service.processIpca(list);

        assertAll(
            () -> verify(ipcaRepository).findFirstByOrderByReferenceDateDesc(),
            () -> verify(ipcaRepository).saveAllAndFlush(any()),
            () -> verifyNoInteractions(selicRepository)
        );
    }

    @Test
    void shouldInsertNewSelicRates() {
        when(selicRepository.findFirstByOrderByRangeInitialDateDesc()).thenReturn(Optional.empty());

        service.processSelic(List.of(getSelic()));

        assertAll(
            () -> verify(selicRepository).findFirstByOrderByRangeInitialDateDesc(),
            () -> verify(selicRepository).saveAllAndFlush(any()),
            () -> verifyNoInteractions(ipcaRepository)
        );
    }

    @ParameterizedTest
    @MethodSource("com.hss.investment.util.InvestmentDTOsMock#getProcessingSelicLists")
    void shouldValidateExistingSelicRates(List<Selic> list) {
        when(selicRepository.findFirstByOrderByRangeInitialDateDesc()).thenReturn(Optional.of(getSelic()));

        service.processSelic(list);

        assertAll(
            () -> verify(selicRepository).findFirstByOrderByRangeInitialDateDesc(),
            () -> verify(selicRepository).saveAllAndFlush(any()),
            () -> verifyNoInteractions(ipcaRepository)
        );
    }

    @Test
    void shouldRetrieveIpcaTimeline() {
        when(ipcaRepository.findByReferenceDateBetween(any(), any())).thenReturn(getRateQueryResultList());

        var timeline = service.getIpcaTimeline(getInvestmentRange());

        assertAll(
            () -> verifyNoInteractions(selicRepository),
            () -> verify(ipcaRepository).findByReferenceDateBetween(any(), any()),
            () -> assertThat(timeline, hasSize(1))
        );
    }

    @Test
    void shouldRetrieveSelicTimeline() {
        when(selicRepository.findByReferenceDateBetween(any(), any())).thenReturn(getRateQueryResultList());

        var timeline = service.getSelicTimeline(getInvestmentRange());

        assertAll(
            () -> verifyNoInteractions(ipcaRepository),
            () -> verify(selicRepository).findByReferenceDateBetween(any(), any()),
            () -> assertThat(timeline, hasSize(1))
        );
    }
}