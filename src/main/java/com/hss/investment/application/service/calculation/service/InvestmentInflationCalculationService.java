package com.hss.investment.application.service.calculation.service;

import com.hss.investment.application.dto.calculation.InvestmentCalculationIPCA;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public final class InvestmentInflationCalculationService extends InvestmentCalculationService<InvestmentCalculationIPCA> {

    // IPCA → taxa de maio é divulgada em junho e somente é aplicada em julho
    // Idaily = (1+anual rate)^(1/360)-1
    // Assim como o cálculo é feito:
    // (1+IPCA) * (1+prefixado) -1
    @Override
    public BigDecimal calculateProfitReturn(InvestmentCalculationIPCA investment) {
        return null;
    }
}