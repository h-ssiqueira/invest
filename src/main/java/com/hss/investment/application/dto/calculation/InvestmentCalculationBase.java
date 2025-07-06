package com.hss.investment.application.dto.calculation;

import java.math.BigDecimal;
import com.hss.investment.domain.model.InvestmentRange;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true, fluent = true)
public abstract sealed class InvestmentCalculationBase permits InvestmentCalculationSimple {

    private BigDecimal amount;
    private InvestmentRange investmentRange;
}