package com.hss.investment.application.service;

import com.hss.investment.application.dto.InvestmentQueryDTO;
import com.hss.openapi.model.InvestmentRequest;
import com.hss.openapi.model.InvestmentResultResponseDTO;
import com.hss.openapi.model.PartialInvestmentResultData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public sealed interface InvestmentService permits InvestmentServiceImpl {

    @Transactional(readOnly = true)
    List<InvestmentResultResponseDTO> retrieveInvestments(InvestmentQueryDTO dto);

    @Transactional
    PartialInvestmentResultData addInvestments(List<InvestmentRequest> dtoList);
}