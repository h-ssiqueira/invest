package com.hss.investment.application.dto.calculation;

import lombok.Builder;
import lombok.Data;
import com.hss.investment.application.entity.Investment;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true, fluent = true)
public final class SelicTimeline extends InvestmentCalculationSimple {

    private Investment.InvestmentRange investmentRange;
}