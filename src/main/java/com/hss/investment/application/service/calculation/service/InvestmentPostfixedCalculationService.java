package com.hss.investment.application.service.calculation.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationSelic;
import com.hss.investment.application.persistence.HolidayRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Service
public final class InvestmentPostfixedCalculationService extends InvestmentCalculationService<InvestmentCalculationSelic> {

    private final HolidayRepository holidayRepository;

    @Override
    public BigDecimal calculateProfitReturn(InvestmentCalculationSelic investment) {
        var groups = splitWithStreams(
            investment.investmentRange()
                .getInvestmentBusinessDays(retrieveHolidays(investment)),
            investment.selicTimeline().stream()
                .filter(x -> nonNull(x.investmentRange().finalDate()))
                .map(x -> x.investmentRange().finalDate()).toList()
        );
        var i = 0;
        var amount = investment.amount();
        for (var selic : investment.selicTimeline()) {
            var dailyRate = calculateDailyRate(selic.rate()
                    .subtract(BigDecimal.valueOf(.1))
                    .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_EVEN)
                    .multiply(investment.rate()),
                CalculationType.BUSINESS_DAYS);
            amount = calculatePeriodAmount(amount, dailyRate, groups.get(i));
            i++;
        }
        return i < groups.size()
            ? calculatePeriodAmount(amount, investment.selicTimeline().getLast().rate(), groups.get(i))
            : amount;
    }

    public static List<Integer> splitWithStreams(List<LocalDate> dates, List<LocalDate> splitPoints) {
        var sortedSplitPoints = splitPoints.stream().sorted().toList();
        var groups = new ArrayList<Integer>();
        var remainingDates = new ArrayList<>(dates.stream().sorted().toList());
        for (var splitPoint : sortedSplitPoints) {
            var group = remainingDates.stream()
                .filter(date -> date.isBefore(splitPoint))
                .toList();
            groups.add(group.size());
            remainingDates.removeAll(group);
        }
        groups.add(remainingDates.size());
        return groups;
    }

    private List<LocalDate> retrieveHolidays(InvestmentCalculationSelic investment) {
        var holidays = holidayRepository.findByReferenceDateBetween(investment.investmentRange().initialDate(), investment.investmentRange().finalDate());
        return holidays.stream()
            .filter(h -> !SATURDAY.equals(h.getDayOfWeek()) && !SUNDAY.equals(h.getDayOfWeek()))
            .toList();
    }
}
