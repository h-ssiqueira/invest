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
        var days = investment.investmentRange().getInvestmentDays();
        // (1 + annual rate) ^ (1 / 360) - 1
        var dailyRate = investment.rate().plus().add(BigDecimal.ONE).pow(1/360).subtract(BigDecimal.ONE);
        // amount * (1 + daily rate) ^ period
        return investment.amount().multiply(dailyRate.add(BigDecimal.ONE).pow(days));
    }
}