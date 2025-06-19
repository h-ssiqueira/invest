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
import com.hss.openapi.model.PartialInvestmentResultDataItemsInner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.hss.investment.application.service.validator.DateValidator.validateInitialAndFinalDates;

@Service
@Slf4j
@RequiredArgsConstructor
public non-sealed class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;

    @Override
    public PartialInvestmentResultData addInvestments(List<InvestmentRequest> dtoList) {
        var responseList = new ArrayList<PartialInvestmentResultDataItemsInner>();
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
                responseList.add(investment);
            } catch (InvestmentException ex) {
                responseList.add(new InvestmentErrorResponseDTO());
            }
        });
        investmentRepository.saveAll(items);
        return new PartialInvestmentResultData().items(responseList);
    }

    @Override
    public List<InvestmentResultResponseDTO> retrieveInvestments(InvestmentQueryDTO dto) {
        validateInitialAndFinalDates(dto.initialDate(), dto.finalDate());
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