package com.hss.investment.application.persistence.entity;

import com.hss.investment.application.exception.InvestmentException;
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
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.hss.investment.application.exception.ErrorMessages.INV_002;

@Table(name = "INVESTMENT")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private Investment(String bank, InvestmentType type, InvestmentRange range, BaseRate rate, BigDecimal amount) {
        this.bank = bank;
        this.investmentType = type;
        this.investmentRange = range;
        this.baseRate = rate;
        this.amount = amount;
        this.createdAt = ZonedDateTime.now();
        this.completed = false;
    }

    public static Investment create(String bank, InvestmentType type, InvestmentRange range, BaseRate rate, BigDecimal amount) {
        return new Investment(bank,type,range,rate,amount);
    }

    @Getter
    @AllArgsConstructor
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
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode
    public static class InvestmentRange {

        @Column(name = "INITIAL_DATE", nullable = false, updatable = false)
        private LocalDate initialDate;

        @Column(name = "FINAL_DATE", nullable = false, updatable = false)
        private LocalDate finalDate;

        public static InvestmentRange of(LocalDate initialDate, LocalDate finalDate) {
            if(initialDate.isAfter(finalDate)) {
                throw new InvestmentException(INV_002);
            }
            return new InvestmentRange(initialDate, finalDate);
        }

        public Integer getInvestmentDays() {
            return initialDate.until(finalDate).getDays();
        }
    }

    @Getter
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class BaseRate {
        
        @Enumerated(EnumType.STRING)
        @Column(name = "ALIQUOT", nullable = false, updatable = false)
        private AliquotType aliquot;
        
        @AttributeOverride(name = "rate", column = @Column(name = "RATE", nullable = false, updatable = false, precision = 10, scale = 2))
        private Percentage rate;

        public static BaseRate of(AliquotType type, BigDecimal rate) {
            return new BaseRate(type, Percentage.of(rate));
        }
    }
}