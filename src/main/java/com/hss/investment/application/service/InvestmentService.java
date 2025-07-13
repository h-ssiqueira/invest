package com.hss.investment.application.service;

import com.hss.investment.application.dto.InvestmentQueryDTO;
import com.hss.openapi.model.InvestmentRequest;
import com.hss.openapi.model.InvestmentResultResponseDTO;
import com.hss.openapi.model.PartialInvestmentResultData;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public sealed interface InvestmentService permits InvestmentServiceImpl {

    @Transactional(readOnly = true)
    List<InvestmentResultResponseDTO> retrieveInvestments(InvestmentQueryDTO dto);

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    PartialInvestmentResultData addInvestments(List<InvestmentRequest> dtoList);
}