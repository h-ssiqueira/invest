package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Ipca;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class IpcaCustomRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private IpcaCustomRepositoryImpl repository;

    @Test
    void insertAllMissingRates() {
        var ipcaList = List.of(
            Ipca.of(LocalDate.of(2023, 1, 1), BigDecimal.valueOf(0.5)),
            Ipca.of(LocalDate.of(2023, 2, 1), BigDecimal.valueOf(0.6))
        );

        repository.insertAllMissingRates(ipcaList);

        verify(jdbcTemplate).update(any(String.class), any(Object.class), any(Object.class), any(Object.class), any(Object.class));
    }

    @Test
    void shouldNotInsertAllMissingRates() {
        repository.insertAllMissingRates(List.of());

        verifyNoInteractions(jdbcTemplate);
    }
}