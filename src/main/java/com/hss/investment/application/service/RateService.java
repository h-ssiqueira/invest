package com.hss.investment.application.service;

import com.hss.investment.dto.RateQueryDTO;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public sealed interface RateService permits RateServiceImpl {

    @Transactional(readOnly = true)
    List<RateResponseWrapperDataItemsInner> retrieveRates(RateQueryDTO dto);
}