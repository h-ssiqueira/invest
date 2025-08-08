package com.hss.investment.application.dto.calculation;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Accessors(chain = true, fluent = true)
public sealed class InvestmentCalculationSimple
    extends InvestmentCalculationBase
    permits InvestmentCalculationSelic, InvestmentCalculationIPCA, SelicTimeline, IPCATimeline {

    private BigDecimal rate;
}