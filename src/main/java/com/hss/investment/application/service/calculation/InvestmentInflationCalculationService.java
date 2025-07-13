package com.hss.investment.application.service.calculation;

import com.hss.investment.application.dto.calculation.InvestmentCalculationIPCA;
import java.math.BigDecimal;

//@Service
public class InvestmentInflationCalculationService extends InvestmentCalculationService<InvestmentCalculationIPCA> {

    // IPCA → taxa de maio é divulgada em junho e somente é aplicada em julho
    // Idaily = (1+anual rate)^(1/360)-1
    // Assim como o cálculo é feito:
    // (1+IPCA) * (1+prefixado) -1
    @Override
    public BigDecimal calculateProfitReturn(InvestmentCalculationIPCA investment) {
        return null;
    }
}