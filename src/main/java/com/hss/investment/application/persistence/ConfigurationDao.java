package com.hss.investment.application.persistence;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConfigurationDao {

    private final JdbcTemplate jdbcTemplate;

    public Optional<ZonedDateTime> getLastUpdatedTimestamp() {
        try {
            var result = jdbcTemplate.query(
                "SELECT last_rate_update FROM configuration LIMIT 1",
                ((rs, rowNum) -> {
                    var odt = rs.getObject("last_rate_update", OffsetDateTime.class);
                    return odt != null ? odt.toZonedDateTime() : ZonedDateTime.now().minusDays(5);
                }));
            return result.stream().findFirst();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void saveLastRateUpdate(ZonedDateTime lastRateUpdate) {
        if (getLastUpdatedTimestamp().isPresent()) {
            jdbcTemplate.update(
                "UPDATE configuration SET last_rate_update = ?",
                Timestamp.from(lastRateUpdate.toInstant())
            );
        } else {
            jdbcTemplate.update(
                "INSERT INTO configuration (last_rate_update) VALUES (?)",
                Timestamp.from(lastRateUpdate.toInstant())
            );
        }
    }

    public Optional<LocalDate> getLastInvestmentUpdated() {
        try {
            var result = jdbcTemplate.query(
                "SELECT last_investment_update FROM configuration LIMIT 1",
                ((rs, rowNum) -> rs.getObject("last_investment_update", LocalDate.class)));
            return result.stream().findFirst();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void saveLastInvestmentUpdate(LocalDate date) {
        if (getLastInvestmentUpdated().isPresent()) {
            jdbcTemplate.update("UPDATE configuration SET last_investment_update = ?", date);
        } else {
            jdbcTemplate.update("INSERT INTO configuration (last_investment_update) VALUES (?)", date);
        }
    }
}
