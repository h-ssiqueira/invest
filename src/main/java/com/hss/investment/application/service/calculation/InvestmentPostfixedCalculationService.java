package com.hss.investment.application.service.calculation;

import com.hss.investment.application.dto.calculation.InvestmentCalculationSelic;
import com.hss.investment.application.persistence.HolidayRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InvestmentPostfixedCalculationService extends InvestmentCalculationService<InvestmentCalculationSelic> {

    private final HolidayRepository holidayRepository;

    // Idaily = (1+anual rate)^(1/252)-1
    @Override
    public BigDecimal calculateProfitReturn(InvestmentCalculationSelic investment) {
        var holidays = holidayRepository.findByReferenceDateBetween(investment.investmentRange().initialDate(), investment.investmentRange().finalDate());
        return null;
    }
}