package com.hss.investment.application.service;

public sealed interface RateKaggleUpdater permits RateKaggleUpdaterImpl {

    void processRates();

    void retrieveAndUpdateRates()
}