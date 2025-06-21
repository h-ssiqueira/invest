package com.hss.investment.application.service.validator;

import com.hss.investment.application.exception.InvestmentException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.hss.investment.application.exception.ErrorMessages.INV_002;
import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateValidator {

    public static void validateInitialAndFinalDates(LocalDate initialDate, LocalDate finalDate){
        if (nonNull(initialDate) && nonNull(finalDate) && initialDate.isAfter(finalDate)) {
            throw new InvestmentException(INV_002);
        }
    }
}