package com.hss.investment.application.persistence;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationDaoTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ConfigurationDao dao;

    @Test
    void getLastUpdatedTimestamp() {
        when(jdbcTemplate.query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any())).thenReturn(singletonList(ZonedDateTime.now()));

        var date = dao.getLastUpdatedTimestamp();

        assertAll(
            () -> verify(jdbcTemplate).query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any()),
            () -> assertThat(date).isNotNull()
        );
    }

    @Test
    void getLastUpdatedTimestampWithException() {
        when(jdbcTemplate.query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any())).thenThrow(EmptyResultDataAccessException.class);

        var date = dao.getLastUpdatedTimestamp();

        assertAll(
            () -> verify(jdbcTemplate).query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any()),
            () -> assertThat(date).isEmpty()
        );
    }

    @Test
    void saveLastRateUpdateWhenAlreadyExists() {
        when(jdbcTemplate.query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any())).thenReturn(singletonList(ZonedDateTime.now()));

        dao.saveLastRateUpdate(ZonedDateTime.now());

        assertAll(
            () -> verify(jdbcTemplate).query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any()),
            () -> verify(jdbcTemplate).update(eq("UPDATE configuration SET last_rate_update = ?"), any(Timestamp.class))
        );
    }

    @Test
    void saveLastRateUpdateWhenDoesNotExists() {
        dao.saveLastRateUpdate(ZonedDateTime.now());

        assertAll(
            () -> verify(jdbcTemplate).query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any()),
            () -> verify(jdbcTemplate).update(eq("INSERT INTO configuration (last_rate_update) VALUES (?)"), any(Timestamp.class))
        );
    }

    @Test
    void getLastInvestmentUpdated() {
        when(jdbcTemplate.query(any(String.class), ArgumentMatchers.<RowMapper<LocalDate>>any())).thenReturn(singletonList(LocalDate.now()));

        var date = dao.getLastInvestmentUpdated();

        assertAll(
            () -> verify(jdbcTemplate).query(any(String.class), ArgumentMatchers.<RowMapper<LocalDate>>any()),
            () -> assertThat(date).isNotNull()
        );
    }

    @Test
    void getLastInvestmentUpdatedWithException() {
        when(jdbcTemplate.query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any())).thenThrow(EmptyResultDataAccessException.class);

        var date = dao.getLastInvestmentUpdated();

        assertAll(
            () -> verify(jdbcTemplate).query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any()),
            () -> assertThat(date).isEmpty()
        );
    }

    @Test
    void saveLastInvestmentUpdateWhenAlreadyExists() {
        when(jdbcTemplate.query(any(String.class), ArgumentMatchers.<RowMapper<LocalDate>>any())).thenReturn(singletonList(LocalDate.now()));

        dao.saveLastInvestmentUpdate(LocalDate.now());

        assertAll(
            () -> verify(jdbcTemplate).query(any(String.class), ArgumentMatchers.<RowMapper<LocalDate>>any()),
            () -> verify(jdbcTemplate).update(eq("UPDATE configuration SET last_investment_update = ?"), any(LocalDate.class))
        );
    }

    @Test
    void saveLastInvestmentUpdateWhenDoesNotExists() {
        dao.saveLastInvestmentUpdate(LocalDate.now());

        assertAll(
            () -> verify(jdbcTemplate).query(any(String.class), ArgumentMatchers.<RowMapper<ZonedDateTime>>any()),
            () -> verify(jdbcTemplate).update(eq("INSERT INTO configuration (last_investment_update) VALUES (?)"), any(LocalDate.class))
        );
    }
}