package com.hss.investment.application.service;

import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public sealed interface RateKaggleUpdater permits RateKaggleUpdaterImpl {

    void processRates();

    @Transactional
    void retrieveAndUpdateRates();

    @Transactional(readOnly = true)
    Optional<ZonedDateTime> retrieveLastUpdateTimestamp();
}