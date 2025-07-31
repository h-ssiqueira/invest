package com.hss.investment.application.service.calculation.processor;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.InvestmentCalculationSelic;
import com.hss.investment.application.dto.calculation.ProfitReturnDTO;
import com.hss.investment.application.service.calculation.service.InvestmentPostfixedCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public final class SelicProcessor implements InvestmentCalculationProcessor<InvestmentCalculationBase> {

    private final InvestmentPostfixedCalculationService service;

    @Override
    public ProfitReturnDTO process(InvestmentCalculationBase dto) {
        return service.calculateInvestment((InvestmentCalculationSelic)dto);
    }

    @Override
    public boolean accepts(InvestmentCalculationBase dto) {
        return dto.getClass() == InvestmentCalculationSelic.class;
    }
}
