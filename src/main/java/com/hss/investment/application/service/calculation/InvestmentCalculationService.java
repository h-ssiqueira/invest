package com.hss.investment.application.service.calculation;

public abstract class InvestmentCalculationService<T extends InvestmentCalculationBase> {

    private final HolidayRepository holidayRepository;

    public ProfitReturnDTO calculateInvestment(T investment) {
        // search for holidays in the database
        var holidays = holidayRepository.findByReferenceDateBetween(investment.getStartDate(), investment.getEndDate());
        // Idaily = (1+anual rate)^(1/252)-1
        return calculateProfitReturn(investment, holidays);
    }

    public abstract ProfitReturnDTO calculateProfitReturn(T investment, List<LocalDate> holidays);
}