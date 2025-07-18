package com.hss.investment.application.service.calculation.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationSelic;
import com.hss.investment.application.persistence.HolidayRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public final class InvestmentPostfixedCalculationService extends InvestmentCalculationService<InvestmentCalculationSelic> {

    private final HolidayRepository holidayRepository;

    @Override
    public BigDecimal calculateProfitReturn(InvestmentCalculationSelic investment) {
        var holidays = holidayRepository.findByReferenceDateBetween(investment.investmentRange().initialDate(), investment.investmentRange().finalDate());
        investment.selicTimeline().forEach(selic -> {
            var selicRate = calculateDailyRate(selic.rate(), CalculationType.BUSINESS_DAYS);
        });
        //var days = calculateBusinessDaysOfMonth(holidays,investment.investmentRange());

        return null;
    }
}