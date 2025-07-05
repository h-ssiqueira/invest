package com.hss.investment.application.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;
import java.util.Optional;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import java.time.ZonedDateTime;

@Repository
@RequiredArgsConstructor
public class ConfigurationDao {

    private final JdbcTemplate jdbcTemplate;

    public Optional<ZonedDateTime> getLastUpdatedTimestamp() {
        try {
            var result = jdbcTemplate.queryForObject(
                "SELECT last_rate_update FROM configuration LIMIT 1",
                ZonedDateTime.class);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void save(ZonedDateTime lastRateUpdate) {
        if (getLastUpdatedTimestamp().ifPresent()) {
            update(lastRateUpdate);
        } else {
            insert(lastRateUpdate);
        }
    }

    private void insert(ZonedDateTime value) {
        jdbcTemplate.update(
            "INSERT INTO configuration (last_rate_update) VALUES (?)",
            Timestamp.from(value.toInstant())
        );
    }

    private void update(ZonedDateTime value) {
        jdbcTemplate.update(
            "UPDATE configuration SET last_rate_update = ?",
            Timestamp.from(value.toInstant())
        );
    }
}
