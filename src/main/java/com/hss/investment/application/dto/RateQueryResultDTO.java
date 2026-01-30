package com.hss.investment.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class RateQueryResultDTO {
    private BigDecimal rate;
    private LocalDate initialDate;
    private LocalDate finalDate;

    @SuppressWarnings("unused")
    public RateQueryResultDTO(BigDecimal rate, LocalDate initialDate) {
        this.rate = rate;
        this.initialDate = initialDate;
    }
}