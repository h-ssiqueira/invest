package com.hss.investment.application.service;

import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.dto.calculation.IPCATimeline;
import com.hss.investment.application.dto.calculation.SelicTimeline;
import com.hss.investment.application.persistence.entity.Investment;
import com.hss.investment.application.persistence.entity.Ipca;
import com.hss.investment.application.persistence.entity.Selic;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public sealed interface RateService permits RateServiceImpl {

    @Transactional(readOnly = true)
    List<RateResponseWrapperDataItemsInner> retrieveRates(RateQueryDTO dto);

    @Transactional(readOnly = true)
    List<IPCATimeline> getIpcaTimeline(Investment.InvestmentRange investmentRange);

    @Transactional(readOnly = true)
    List<SelicTimeline> getSelicTimeline(Investment.InvestmentRange investmentRange);

    @Transactional
    void processIpca(List<Ipca> rateList);

    @Transactional
    void processSelic(List<Selic> rateList);
}