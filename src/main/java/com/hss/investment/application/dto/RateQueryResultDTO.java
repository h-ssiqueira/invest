package com.hss.investment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RateQueryResultDTO {
    private BigDecimal rate;
    private LocalDate initialDate;
    private LocalDate finalDate;
}