package com.hss.investment.application.dto.calculation;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Accessors(chain = true, fluent = true)
public final class InvestmentCalculationIPCA extends InvestmentCalculationSimple {
    
    private List<IPCATimeline> ipcaTimeline;
}