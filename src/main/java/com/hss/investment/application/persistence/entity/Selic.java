package com.hss.investment.application.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "SELIC")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(exclude = "id")
public class Selic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, updatable = false, nullable = false)
    private Integer id;

    @AttributeOverride(name = "initialDate", column = @Column(name = "REFERENCE_START_DATE", unique = true, updatable = false, nullable = false))
    @AttributeOverride(name = "finalDate", column = @Column(name = "REFERENCE_END_DATE", unique = true))
    private Investment.InvestmentRange range;

    @Embedded
    private Percentage rate;

    private Selic(LocalDate startDate, LocalDate finalDate, Percentage rate) {
        this.rate = rate;
        this.range = Investment.InvestmentRange.of(startDate, finalDate);
    }

    public static Selic of(LocalDate startDate, LocalDate finalDate, BigDecimal rate) {
        return new Selic(startDate, finalDate, Percentage.of(rate));
    }
}