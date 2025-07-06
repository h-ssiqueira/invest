package com.hss.investment.application.dto.calculation;

import java.math.BigDecimal;

public record ProfitReturnDTO(BigDecimal expectedEarnings, BigDecimal earnedAmount, BigDecimal profit) {

}