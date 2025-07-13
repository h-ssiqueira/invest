package com.hss.investment.application.service.calculation;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.ProfitReturnDTO;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public abstract class InvestmentCalculationService<T extends InvestmentCalculationBase> {

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