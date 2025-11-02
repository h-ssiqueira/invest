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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.hss.investment.application.exception.ErrorMessages.INV_002;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Table(name = "INVESTMENT")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(exclude = {"id","createdAt"})
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

    @Embedded
    private ProfitResult profitResult;

    private Investment(String bank, InvestmentType type, InvestmentRange range, BaseRate rate, BigDecimal amount) {
        this.bank = bank;
        this.investmentType = type;
        this.investmentRange = range;
        this.baseRate = rate;
        this.amount = amount;
        this.createdAt = ZonedDateTime.now();
        this.completed = investmentRange.isCompleted();
    }

    public static Investment create(String bank, InvestmentType type, InvestmentRange range, BaseRate rate, BigDecimal amount) {
        return new Investment(bank,type,range,rate,amount);
    }

    public void terminateInvestment(ProfitResult profitResult) {
        if(isNull(investmentRange().finalDate())) {
            this.investmentRange.finalDate = LocalDate.now();
        }
        this.profitResult = profitResult;
        this.completed = true;
    }

    public double amountFormatted() {
        return amount().setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public Double getTaxFormatted() {
        return investmentType().hasTaxes() ? investmentRange().getTaxFormatted() : null;
    }

    @Getter
    @Accessors(fluent = true)
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
    @Accessors(fluent = true)
    @EqualsAndHashCode
    public static class InvestmentRange {

        @Column(name = "INITIAL_DATE", nullable = false, updatable = false)
        private LocalDate initialDate;

        @Setter
        @Accessors(chain = true)
        @Column(name = "FINAL_DATE", nullable = false)
        private LocalDate finalDate;

        public static InvestmentRange of(LocalDate initialDate, LocalDate finalDate) {
            if(nonNull(finalDate) && initialDate.isAfter(finalDate)) {
                throw new InvestmentException(INV_002);
            }
            return new InvestmentRange(initialDate, finalDate);
        }

        private LocalDate retrieveFinalDate() {
            return isNull(finalDate()) ? LocalDate.now() : finalDate();
        }

        public YearMonth finalDateYearMonth() {
            var finalDate = retrieveFinalDate();
            return YearMonth.of(finalDate.getYear(),finalDate.getMonth());
        }

        public YearMonth initialDateYearMonth() {
            return YearMonth.of(initialDate().getYear(),initialDate().getMonth());
        }

        public List<LocalDate> getInvestmentBusinessDays(List<LocalDate> holiday) {
            return initialDate().datesUntil(retrieveFinalDate())
                .filter(day ->
                    !SATURDAY.equals(day.getDayOfWeek()) && !SUNDAY.equals(day.getDayOfWeek()) && !holiday.contains(day)
                ).toList();
        }

        public int getInvestmentDays() {
            return Math.toIntExact(initialDate().until(retrieveFinalDate(), ChronoUnit.DAYS));
        }

        public BigDecimal getTax() {
            var daysInvested = getInvestmentDays();
            if(daysInvested <= 180)
                return BigDecimal.valueOf(.225);
            if (daysInvested <= 360)
                return BigDecimal.valueOf(.2);
            if (daysInvested <= 720)
                return BigDecimal.valueOf(.175);
            return BigDecimal.valueOf(.15);
        }

        public Double getTaxFormatted() {
            return getTax().setScale(4, RoundingMode.HALF_EVEN).doubleValue() * 100;
        }

        public boolean isCompleted() {
            return nonNull(finalDate()) && finalDate().isBefore(LocalDate.now());
        }

        public int retrieveDaysFromMonth(YearMonth month) {
            if(month.equals(initialDateYearMonth())) {
                return initialDate().lengthOfMonth() - initialDate().getDayOfMonth() + 1;
            }
            if(month.equals(finalDateYearMonth())) {
                return retrieveFinalDate().getDayOfMonth();
            }
            return month.lengthOfMonth();
        }

        public int retrieveDaysFromPeriod(YearMonth month) {
            return Math.toIntExact(month.atDay(1).until(retrieveFinalDate(), ChronoUnit.DAYS));
        }
    }

    @Getter
    @Accessors(fluent = true)
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

    @Getter
    @Accessors(fluent = true)
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class ProfitResult {

        @Column(name = "EARNING_AMOUNT")
        private BigDecimal earningAmount;

        @Column(name = "PROFIT_AMOUNT")
        private BigDecimal profitAmount;

        @Column(name = "TAX_AMOUNT")
        private BigDecimal taxAmount;

        public static ProfitResult of(BigDecimal earned, BigDecimal profit) {
            return new ProfitResult(earned, profit, earned.subtract(profit));
        }
    }
}