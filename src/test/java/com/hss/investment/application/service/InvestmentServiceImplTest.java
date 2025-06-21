package com.hss.investment.application.service;

import com.hss.investment.application.persistence.InvestmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRequestList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceImplTest {

    @Mock
    private InvestmentRepository repository;

    @InjectMocks
    private InvestmentServiceImpl service;

    @Test
    void addInvestments() {
        var response = service.addInvestments(getInvestmentRequestList());

        assertAll(
            () -> assertThat(response.getItems(), hasSize(2)),
            () -> verify(repository).saveAll(any())
        );
    }
}