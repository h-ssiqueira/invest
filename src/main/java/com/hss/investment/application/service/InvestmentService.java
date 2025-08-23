package com.hss.investment.application.service;

import com.hss.investment.application.dto.InvestmentQueryDTO;
import com.hss.openapi.model.InvestmentRequest;
import com.hss.openapi.model.InvestmentResultResponseDTO;
import com.hss.openapi.model.InvestmentSimulationResultResponseDTO;
import com.hss.openapi.model.PartialInvestmentResultData;
import com.hss.openapi.model.SimulationInvestmentRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public sealed interface InvestmentService permits InvestmentServiceImpl {

    @Transactional(readOnly = true)
    List<InvestmentResultResponseDTO> retrieveInvestments(InvestmentQueryDTO dto);

    PartialInvestmentResultData addInvestments(List<InvestmentRequest> dtoList);

    @Transactional(readOnly = true)
    InvestmentSimulationResultResponseDTO simulateInvestment(SimulationInvestmentRequest dto);

    void completeInvestment(UUID id);

    @Scheduled(cron = "@daily")
    @EventListener(ApplicationReadyEvent.class)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    void updateInvestments();
}