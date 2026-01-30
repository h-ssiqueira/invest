package com.hss.investment.application.persistence;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainerProvider;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Testcontainers
@SuppressWarnings("unused")
class HolidayRepositoryTest {

    @Container
    private static final JdbcDatabaseContainer<?> postgres = new PostgreSQLContainerProvider().newInstance("17");

    @DynamicPropertySource
    private static void datasourceConfig(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.properties.hibernate.generate_statistics", () -> "true");
    }

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private EntityManagerFactory emf;

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }

    @Test
    void findByReferenceDateBetween() {
        var stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        var result = holidayRepository.findByReferenceDateBetween(LocalDate.of(2000,1,1), LocalDate.of(2010,1,1));

        assertAll(
            () -> assertThat(result).isNotEmpty().hasSize(92),
            () -> assertThat(stats.getPrepareStatementCount()).isEqualTo(1),
            () -> assertThat(stats.getQueryExecutionCount()).isEqualTo(1)
        );

    }
}