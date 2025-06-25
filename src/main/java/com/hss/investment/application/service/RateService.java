package com.hss.investment.application.service;

import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.persistence.entity.Ipca;
import com.hss.investment.application.persistence.entity.Selic;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public sealed interface RateService permits RateServiceImpl {

    @Transactional(readOnly = true)
    List<RateResponseWrapperDataItemsInner> retrieveRates(RateQueryDTO dto);

    @Transactional
    void processIpca(List<Ipca> rateList);

    @Transactional
    void processSelic(List<Selic> rateList);
}