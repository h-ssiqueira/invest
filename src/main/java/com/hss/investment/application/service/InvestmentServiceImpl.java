package com.hss.investment.application.service;

import com.hss.investment.application.dto.InvestmentQueryDTO;
import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.InvestmentRepository;
import com.hss.investment.application.persistence.entity.Investment;
import com.hss.openapi.model.InvestmentErrorResponseDTO;
import com.hss.openapi.model.InvestmentRequest;
import com.hss.openapi.model.InvestmentResultResponseDTO;
import com.hss.openapi.model.InvestmentType;
import com.hss.openapi.model.PartialInvestmentResultData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.hss.investment.application.exception.ErrorMessages.INV_002;

@Service
@Slf4j
@RequiredArgsConstructor
public non-sealed class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;

    @Override
    public PartialInvestmentResultData addInvestments(List<InvestmentRequest> dtoList) {
        var response = new PartialInvestmentResultData();
        var items = new ArrayList<Investment>();
        dtoList.forEach(investment -> {
            try {
                var entity = Investment.create(
                    investment.getBank(),
                    Investment.InvestmentType.valueOf(investment.getType().getValue()),
                    Investment.InvestmentRange.of(investment.getInitialDate(), investment.getFinalDate()),
                    Investment.BaseRate.of(Investment.AliquotType.valueOf(investment.getAliquot().getValue()), BigDecimal.valueOf(investment.getRate())),
                    BigDecimal.valueOf(investment.getAmount())
                );
                items.add(entity);
                response.addItemsItem(investment);
            } catch (InvestmentException ex) {
                response.addItemsItem(new InvestmentErrorResponseDTO());
            }
        });
        investmentRepository.saveAll(items);
        return response;
    }

    @Override
    public List<InvestmentResultResponseDTO> retrieveInvestments(InvestmentQueryDTO dto) {
        if(dto.initialDate().isAfter(dto.finalDate())) {
            throw new InvestmentException(INV_002);
        }
        var result = investmentRepository.findByParameters(dto, dto.page());
        return result.stream()
            .map(item -> new InvestmentResultResponseDTO()
                .bank(item.getBank())
                .amount(item.getAmount().doubleValue())
                .initialDate(item.getInvestmentRange().getInitialDate())
                .finalDate(item.getInvestmentRange().getFinalDate())
                .type(InvestmentType.valueOf(item.getInvestmentType().name()))
                .rate(item.getBaseRate().getRate().getRate().floatValue())
            ).toList();
    }
}