package com.hss.investment.application.service.calculation.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.ProfitReturnDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.nevec.rjm.BigDecimalMath;

@RequiredArgsConstructor
public abstract sealed class InvestmentCalculationService<T extends InvestmentCalculationBase>
    permits InvestmentInflationCalculationService, InvestmentPostfixedCalculationService, InvestmentPrefixedCalculationService {

    public ProfitReturnDTO calculateInvestment(T investment) {
        var finalAmount = calculateProfitReturn(investment);
        var taxes = calculateTaxes(investment, finalAmount);
        return new ProfitReturnDTO(finalAmount,finalAmount.subtract(investment.amount()).subtract(taxes));
    }

    private BigDecimal calculateTaxes(T investment, BigDecimal finalAmount) {
        return investment.type().hasTaxes() ?
            finalAmount.subtract(investment.amount())
            .multiply(investment.investmentRange().getTax())
            : BigDecimal.ZERO;
    }

    /**
     * (1 + yearly rate) ^ (1/type) - 1
     * @param rate rate in decimal
     * @param type period in days
     * @return the investment daily rate within the period
     */
    protected BigDecimal calculateDailyRate(BigDecimal rate, CalculationType type) {
        return BigDecimalMath.pow(BigDecimal.ONE.add(rate),
                BigDecimal.ONE.divide(BigDecimal.valueOf(type.days()), 10, RoundingMode.HALF_EVEN)
            ).subtract(BigDecimal.ONE);
    }

    /**
     * amount * (1 + daily rate) ^ period
     * @param amount amount invested
     * @param rate rate in decimal
     * @param period period in days
     * @return the amount earned after period
    */
    protected BigDecimal calculatePeriodAmount(BigDecimal amount, BigDecimal rate, int period) {
        return amount
            .multiply(rate
                .add(BigDecimal.ONE)
                .pow(period));
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    protected enum CalculationType {
        STRAIGHT(360),
        BUSINESS_DAYS(252);

        private final int days;
    }

    public abstract BigDecimal calculateProfitReturn(T investment);
}