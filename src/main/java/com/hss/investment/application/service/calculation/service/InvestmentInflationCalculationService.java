package com.hss.investment.application.service.calculation.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationIPCA;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public final class InvestmentInflationCalculationService extends InvestmentCalculationService<InvestmentCalculationIPCA> {

    /**
     * IPCA → May's rate is published in June and applied only in July
     */
    @Override
    public BigDecimal calculateProfitReturn(InvestmentCalculationIPCA investment) {
        var fixedDailyRate = calculateDailyRate(investment.rate(), CalculationType.STRAIGHT);
        var amount = investment.amount();
        for (var ipca : investment.ipcaTimeline()) {
            amount = calculatePeriodAmount(
                amount,
                calculateIPCARate(ipca.rate(), fixedDailyRate),
                investment.investmentRange().retrieveDaysFromMonth(ipca.month().plusMonths(2))
            );
        }
        var last = investment.ipcaTimeline().getLast();
        if (!last.month().equals(investment.investmentRange().finalDateYearMonth())) {
            amount = calculatePeriodAmount(
                amount,
                calculateIPCARate(last.rate(), fixedDailyRate),
                investment.investmentRange().retrieveDaysFromPeriod(last.month().plusMonths(3))
            );
        }
        return amount;
    }

    /**
     * (1 + IPCA) * (1 + prefixed) - 1
     *
     * @param ipcaRate       in decimal
     * @param fixedDailyRate in decimal
     * @return the ipca rate of month
     */
    private BigDecimal calculateIPCARate(BigDecimal ipcaRate, BigDecimal fixedDailyRate) {
        return BigDecimal.ONE.add(
                calculateDailyRate(
                    ipcaRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_EVEN),
                    CalculationType.STRAIGHT))
            .multiply(BigDecimal.ONE.add(fixedDailyRate))
            .subtract(BigDecimal.ONE);
    }
}