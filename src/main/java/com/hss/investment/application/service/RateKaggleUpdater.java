package com.hss.investment.application.service;

import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.Optional;

public sealed interface RateKaggleUpdater permits RateKaggleUpdaterImpl {

    void processRates();

    @Transactional
    void retrieveAndUpdateRates();

    @Transactional(readOnly = true)
    Optional<ZonedDateTime> retrieveLastUpdateTimestamp();
}