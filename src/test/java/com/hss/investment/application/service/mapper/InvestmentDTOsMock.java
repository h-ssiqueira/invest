package com.hss.investment.application.service.mapper;

import com.hss.investment.application.dto.RateQueryResultDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvestmentDTOsMock {

    public static List<RateQueryResultDTO> getRateQueryResultDTOList() {
        return List.of(new RateQueryResultDTO(BigDecimal.ONE, LocalDate.now(), LocalDate.now().plusDays(1L)));
    }
}