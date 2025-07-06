package com.hss.investment.application.dto.calculation;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true, fluent = true)
public final class InvestmentCalculationIPCA extends InvestmentCalculationSimple {
    
    private List<IPCATimeline> ipcaTimeline;
}