package com.hss.investment.application.persistence.entity;

import com.hss.investment.application.exception.InvestmentException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.hss.investment.application.exception.ErrorMessages.INV_003;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Percentage {

    @Column(name = "RATE", nullable = false, updatable = false, precision = 10, scale = 2)
    private BigDecimal rate;

    public static Percentage of(BigDecimal rate) {
        if(rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvestmentException(INV_003);
        }
        return new Percentage(rate);
    }

    public BigDecimal ratePercentage() {
        return rate;
    }

    public BigDecimal rateCalculate() {
        return rate.divide(BigDecimal.valueOf(100));
    }

    @Override
    public String toString() {
        return "%s%%".formatted(rate);
    }
}