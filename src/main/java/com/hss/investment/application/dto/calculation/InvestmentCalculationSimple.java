package com.hss.investment.application.dto.calculation;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true, fluent = true)
public sealed class InvestmentCalculationSimple extends InvestmentCalculationBase
permits InvestmentCalculationSelic, InvestmentCalculationIPCA, SelicTimeline, IPCATimeline {

    private BigDecimal rate;
}