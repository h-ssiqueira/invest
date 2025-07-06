package com.hss.investment.application.service.calculation;

public class InvestmentPostfixedCalculationService<InvestmentCalculationSelic> extends InvestmentCalculationService {

    @Override
    public ProfitReturnDTO calculateProfitReturn(InvestmentCalculationSelic investment, List<Holiday> holidays) {
        var profitReturnDTO = new ProfitReturnDTO();
        
        return profitReturnDTO;
    }
}