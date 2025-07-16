package com.hss.investment.application.service.calculation.processor;

import com.hss.investment.application.dto.calculation.ProfitReturnDTO;

public sealed interface InvestmentCalculationProcessor<T> permits IPCAProcessor, PrefixedProcessor, SelicProcessor {

    ProfitReturnDTO process(T dto);

    boolean accepts(T dto);
}
