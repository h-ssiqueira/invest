package com.hss.investment.application.dto;

import java.time.LocalDate;

public record RateQueryDTO(RateType type, LocalDate initialDate, LocalDate finalDate) {
    public enum RateType {
        SELIC,IPCA
    }
}