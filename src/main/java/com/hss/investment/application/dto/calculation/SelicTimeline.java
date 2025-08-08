package com.hss.investment.application.dto.calculation;

import com.hss.investment.application.persistence.entity.Investment.InvestmentRange;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Accessors(chain = true, fluent = true)
public final class SelicTimeline extends InvestmentCalculationSimple {

    private InvestmentRange investmentRange;
}