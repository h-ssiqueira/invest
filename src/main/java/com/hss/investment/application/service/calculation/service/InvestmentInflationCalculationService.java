package com.hss.investment.application.service.calculation.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationIPCA;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public final class InvestmentInflationCalculationService extends InvestmentCalculationService<InvestmentCalculationIPCA> {

    // IPCA → taxa de maio é divulgada em junho e somente é aplicada em julho
    @Override
    public BigDecimal calculateProfitReturn(InvestmentCalculationIPCA investment) {
        var fixedDailyRate = calculateDailyRate(investment.rate(), CalculationType.STRAIGHT);
        var amount = investment.amount();
        for (var ipca : investment.ipcaTimeline()) {
            // (1 + IPCA) * (1 + prefixado) - 1
            var ipcaDailyRate = BigDecimal.ONE.add(calculateDailyRate(ipca.rate(), CalculationType.STRAIGHT))
                .multiply(BigDecimal.ONE.add(fixedDailyRate))
                .subtract(BigDecimal.ONE);
            var period = investment.investmentRange().retrieveDaysFromMonth(ipca.month().plusMonths(2));
            amount = calculatePeriodAmount(amount, ipcaDailyRate, period);
        }
        return amount;
    }
}