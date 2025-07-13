package com.hss.investment.application.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Table(name = "IPCA")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(exclude = "id")
public class Ipca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, updatable = false, nullable = false)
    private Integer id;

    @Column(name = "REFERENCE_DATE", unique = true, updatable = false, nullable = false)
    private LocalDate referenceDate;

    @Embedded
    private Percentage rate;

    private Ipca(LocalDate referenceDate, Percentage rate) {
        this.rate = rate;
        this.referenceDate = referenceDate;
    }

    public static Ipca of(LocalDate referenceDate, BigDecimal rate) {
        return new Ipca(referenceDate, Percentage.of(rate));
    }
}