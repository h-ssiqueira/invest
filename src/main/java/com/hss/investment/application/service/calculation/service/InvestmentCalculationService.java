package com.hss.investment.application.service.calculation.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.ProfitReturnDTO;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract sealed class InvestmentCalculationService<T extends InvestmentCalculationBase>
    permits InvestmentInflationCalculationService, InvestmentPostfixedCalculationService, InvestmentPrefixedCalculationService {

    public ProfitReturnDTO calculateInvestment(T investment) {
        var finalAmount = calculateProfitReturn(investment);
        var taxes = calculateTaxes(investment, finalAmount);
        return new ProfitReturnDTO(finalAmount,finalAmount.subtract(investment.amount()).subtract(taxes));
    }

    private BigDecimal calculateTaxes(T investment, BigDecimal finalAmount) {
        return investment.type().hasTaxes() ? finalAmount.subtract(investment.amount())
            .multiply(investment.investmentRange().getTax()) : BigDecimal.ZERO;
    }

    public abstract BigDecimal calculateProfitReturn(T investment);
}