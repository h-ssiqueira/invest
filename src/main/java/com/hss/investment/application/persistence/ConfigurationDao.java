package com.hss.investment.application.persistence;

import java.sql.Timestamp;
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
                    return odt != null ? odt.toZonedDateTime() : null;
                }));
            return Optional.ofNullable(result.getFirst());
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void save(ZonedDateTime lastRateUpdate) {
        if (getLastUpdatedTimestamp().isPresent()) {
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
