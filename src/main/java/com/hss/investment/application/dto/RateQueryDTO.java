package com.hss.investment.application.dto;

import java.time.LocalDate;
import java.util.Arrays;

public record RateQueryDTO(RateType type, LocalDate initialDate, LocalDate finalDate) {
    public enum RateType {
        SELIC,IPCA;


        public static RateType fromValue(String value) {
            return Arrays.stream(RateType.values())
                .filter(rateType -> rateType.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum value for %s".formatted(value)));
        }
    }
}