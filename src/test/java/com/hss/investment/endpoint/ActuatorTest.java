package com.hss.investment.endpoint;

import com.hss.investment.config.SecurityConfig;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableAutoConfiguration(exclude = {LiquibaseAutoConfiguration.class, DataSourceAutoConfiguration.class})
@AutoConfigureMockMvc
@SpringBootTest(classes = SecurityConfig.class, properties = {
    "management.endpoints.web.exposure.include=*",
    "management.prometheus.metrics.export.enabled=true",
    "management.endpoint.heapdump.enabled=true"
})
@SuppressWarnings("unused")
public class ActuatorTest {

    @Autowired
    private MockMvc mvc;

    @ParameterizedTest
    @WithAnonymousUser
    @MethodSource("getActuatorEndpoints")
    @DisplayName("Unauthorized endpoint for url")
    void shouldCheckHealthEndpoint(String endpoint) throws Exception {
        mvc.perform(get(endpoint))
            .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @MethodSource("getActuatorEndpoints")
    @DisplayName("Authorized endpoint for url")
    void shouldAccessHealthEndpoint(String endpoint) throws Exception {
        mvc.perform(get(endpoint)
                .with(httpBasic("hss", "hss")))
            .andExpect(status().isOk());
    }

    private static Stream<Arguments> getActuatorEndpoints() {
        return Stream.of(
            Arguments.of("/actuator"),
            Arguments.of("/actuator/health"),
            Arguments.of("/actuator/info"),
            Arguments.of("/actuator/env"),
            Arguments.of("/actuator/loggers"),
            Arguments.of("/actuator/metrics"),
            Arguments.of("/actuator/prometheus"),
            Arguments.of("/actuator/beans"),
            Arguments.of("/actuator/sbom"),
            Arguments.of("/actuator/scheduledtasks"),
            Arguments.of("/actuator/health/readiness"),
            Arguments.of("/actuator/health/liveness"),
            Arguments.of("/actuator/configprops"),
            Arguments.of("/actuator/conditions"),
            Arguments.of("/actuator/mappings"),
            Arguments.of("/actuator/heapdump"),
            Arguments.of("/actuator/threaddump")
        );
    }
}
