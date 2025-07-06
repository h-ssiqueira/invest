package com.hss.investment.application.dto.calculation;

import lombok.Builder;
import lombok.Data;
import java.time.MonthYear;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true, fluent = true)
public final class IPCATimeline extends InvestmentCalculationSimple {

    private MonthYear month;
}