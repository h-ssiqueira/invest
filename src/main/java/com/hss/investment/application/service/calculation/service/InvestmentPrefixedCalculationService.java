package com.hss.investment.application.service.calculation.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationSimple;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public final class InvestmentPrefixedCalculationService extends InvestmentCalculationService<InvestmentCalculationSimple> {

    @Override
    public BigDecimal calculateProfitReturn(InvestmentCalculationSimple investment) {
        return calculatePeriodAmount(
            investment.amount(),
            calculateDailyRate(investment.rate(), CalculationType.STRAIGHT),
            investment.investmentRange().getInvestmentDays());
    }
}