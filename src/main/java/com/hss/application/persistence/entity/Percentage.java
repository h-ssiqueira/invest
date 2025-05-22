package com.hss.application.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Percentage {

    @Column(name = "RATE", nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal rate;

    public BigDecimal getRate() {
        return rate.divide(BigDecimal.valueOf(100));
    }

    @Override
    public String toString() {
        return "%s%%".formatted(rate);
    }
}