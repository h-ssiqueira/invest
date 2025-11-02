package com.hss.investment.application.service.calculation;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.SelicTimeline;
import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.HolidayRepository;
import com.hss.investment.application.service.calculation.processor.IPCAProcessor;
import com.hss.investment.application.service.calculation.processor.PrefixedProcessor;
import com.hss.investment.application.service.calculation.processor.SelicProcessor;
import com.hss.investment.application.service.calculation.service.InvestmentInflationCalculationService;
import com.hss.investment.application.service.calculation.service.InvestmentPostfixedCalculationService;
import com.hss.investment.application.service.calculation.service.InvestmentPrefixedCalculationService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.hss.investment.application.exception.ErrorMessages.INV_006;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InvestmentCalculationDelegatorTest {

    @InjectMocks
    private InvestmentCalculationDelegator<InvestmentCalculationBase> delegator;

    @Mock
    private HolidayRepository holidayRepository;

    @BeforeEach
    void init() {
        delegator = new InvestmentCalculationDelegator<>(List.of(
            new IPCAProcessor(new InvestmentInflationCalculationService()),
            new SelicProcessor(new InvestmentPostfixedCalculationService(holidayRepository)),
            new PrefixedProcessor(new InvestmentPrefixedCalculationService())
        ));
    }

    @ParameterizedTest
    @MethodSource("com.hss.investment.util.InvestmentDTOsMock#getCalculationTypes")
    void shouldProcessAllCalculationTypes(InvestmentCalculationBase dto) {
        assertDoesNotThrow(() -> delegator.delegate(dto));
    }

    @Test
    void shouldProcessCheckWrongClassType() {
        var ex = assertThrows(InvestmentException.class, () -> delegator.delegate(SelicTimeline.builder().build()));

        assertThat(ex.getMessage()).isEqualTo(INV_006.formatted("Processor"));
    }
}