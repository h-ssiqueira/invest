package com.hss.investment.application.service.calculation.processor;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.InvestmentCalculationSimple;
import com.hss.investment.application.dto.calculation.ProfitReturnDTO;
import com.hss.investment.application.service.calculation.service.InvestmentPrefixedCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public final class PrefixedProcessor implements InvestmentCalculationProcessor<InvestmentCalculationBase> {

    private final InvestmentPrefixedCalculationService service;

    @Override
    public ProfitReturnDTO process(InvestmentCalculationBase dto) {
        return service.calculateInvestment((InvestmentCalculationSimple)dto);
    }

    @Override
    public boolean accepts(InvestmentCalculationBase dto) {
        return dto instanceof InvestmentCalculationSimple;
    }
}
