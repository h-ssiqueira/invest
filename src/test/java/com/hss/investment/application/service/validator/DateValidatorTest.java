package com.hss.investment.application.service.validator;

import com.hss.investment.application.exception.InvestmentException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static com.hss.investment.application.exception.ErrorMessages.INV_002;
import static com.hss.investment.application.service.validator.DateValidator.validateInitialAndFinalDates;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateValidatorTest {

    @Test
    void shouldValidateDateCorrectly() {
        assertDoesNotThrow(() -> validateInitialAndFinalDates(LocalDate.now(), LocalDate.now()));
    }

    @Test
    void shouldValidateDateCorrectlyWhenFail() {
        var initialDate = LocalDate.now();
        var finalDate = LocalDate.now().minusDays(10);
        var ex = assertThrows(InvestmentException.class, () -> validateInitialAndFinalDates(initialDate, finalDate));

        assertThat(ex).hasMessage(INV_002);
    }
}