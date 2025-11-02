package com.hss.investment.application.service.calculation;

import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.ProfitReturnDTO;
import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.service.calculation.processor.InvestmentCalculationProcessor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.hss.investment.application.exception.ErrorMessages.INV_006;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvestmentCalculationDelegator<T extends InvestmentCalculationBase> {

    private final List<InvestmentCalculationProcessor<T>> services;

    public ProfitReturnDTO delegate(T dto) {
        return services.stream()
            .filter(service -> service.accepts(dto))
            .findFirst()
            .orElseThrow(() -> new InvestmentException(INV_006.formatted("Processor")))
            .process(dto);
    }
}
