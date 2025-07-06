package com.hss.investment.application.service.calculation;

public class InvestmentInflationCalculationService<InvestmentCalculationIPCA> extends InvestmentCalculationService {

    // IPCA → taxa de maio é divulgada em junho e somente é aplicada em julho
    // Assim como o cálculo é feito:
    // (1+IPCA) * (1+prefixado) -1

    @Override
    public ProfitReturnDTO calculateProfitReturn(InvestmentCalculationIPCA investment, List<Holiday> holidays) {
        var profitReturnDTO = new ProfitReturnDTO();
        
        return profitReturnDTO;
    }
}