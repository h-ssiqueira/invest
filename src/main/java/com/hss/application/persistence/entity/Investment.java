package com.hss.application.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Table(name = "INVESTMENT")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Investment {

    @Id
    @GeneratedValue(generator = "org.hibernate.id.uuid.UuidGenerator")
    @Column(name = "ID", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "BANK", nullable = false, updatable = false, length = 100)
    private String bank;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "INVESTMENT_TYPE", nullable = false, updatable = false)
    private InvestmentType investmentType;
    
    @Embedded
    private InvestmentRange investmentRange;
    
    @Embedded
    private BaseRate baseRate;

    @Column(name = "AMOUNT", nullable = false, updatable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "COMPLETED", nullable = false)
    private boolean completed;

    @AllArgsConstructor
    @Getter
    public enum InvestmentType {
        CDB(true),
        RDB(true),
        LCA(false),
        LCI(false),
        CRA(false),
        CRI(false);
        
        private final boolean hasTaxes;
    }
    
    public enum AliquotType {
        PREFIXED,INFLATION,POSTFIXED
    }

    @Embeddable
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode
    public static class InvestmentRange {

        @Column(name = "INITIAL_DATE", nullable = false, updatable = false)
        private LocalDate initialDate;

        @Column(name = "FINAL_DATE", nullable = false, updatable = false)
        private LocalDate finalDate;

        public Integer getInvestmentDays() {
            return initialDate.until(finalDate).getDays();
        }
    }

    @Getter
    @Embeddable
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class BaseRate {
        
        @Enumerated(EnumType.STRING)
        @Column(name = "ALIQUOT", nullable = false, updatable = false)
        private AliquotType aliquot;
        
        @AttributeOverride(name = "rate", column = @Column(name = "RATE", nullable = false, updatable = false, precision = 10, scale = 2))
        private Percentage rate;
    }
}