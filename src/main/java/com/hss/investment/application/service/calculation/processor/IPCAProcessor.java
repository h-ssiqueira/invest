package com.hss.investment.application.service.calculation.processor;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.InvestmentCalculationIPCA;
import com.hss.investment.application.dto.calculation.ProfitReturnDTO;
import com.hss.investment.application.service.calculation.service.InvestmentInflationCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class IPCAProcessor implements InvestmentCalculationProcessor<InvestmentCalculationBase> {

    private final InvestmentInflationCalculationService service;

    @Override
    public ProfitReturnDTO process(InvestmentCalculationBase dto) {
        return service.calculateInvestment((InvestmentCalculationIPCA) dto);
    }

    @Override
    public boolean accepts(InvestmentCalculationBase dto) {
        return dto instanceof InvestmentCalculationIPCA;
    }
}
