package com.hss.investment.application.dto.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ProfitReturnDTO(BigDecimal earnings, BigDecimal profit) {

    public double earningsFormatted() {
        return earnings().setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public double profitFormatted() {
        return profit().setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }
}