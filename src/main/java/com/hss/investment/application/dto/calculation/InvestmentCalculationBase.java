package com.hss.investment.application.dto.calculation;

import com.hss.investment.application.persistence.entity.Investment;
import com.hss.investment.application.persistence.entity.Investment.InvestmentRange;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode
public abstract sealed class InvestmentCalculationBase permits InvestmentCalculationSimple  {

    private Investment.InvestmentType type;
    private BigDecimal amount;
    private InvestmentRange investmentRange;
}