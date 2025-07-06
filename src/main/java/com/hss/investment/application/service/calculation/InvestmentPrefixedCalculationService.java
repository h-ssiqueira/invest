package com.hss.investment.application.service.calculation;

public class InvestmentPrefixedCalculationService<InvestmentCalculationSimple> extends InvestmentCalculationService {

    @Override
    public ProfitReturnDTO calculateProfitReturn(InvestmentCalculationSimple investment, List<Holiday> holidays) {
        var profitReturnDTO = new ProfitReturnDTO();
        
        return profitReturnDTO;
    }
}