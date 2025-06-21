package com.hss.investment.application.service.mapper;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static com.hss.investment.util.InvestmentDTOsMock.getRateQueryResultDTOList;
import static org.assertj.core.api.Assertions.assertThat;

class GeneralMapperTest {

    private final GeneralMapperImpl mapper = new GeneralMapperImpl();

    @Test
    void toRateResponseWrapperDataItemsInner() {
        var dto = getRateQueryResultDTOList();
        var mapped = mapper.toRateResponseWrapperDataItemsInner(dto);

        assertThat(mapped)
            .isNotEmpty()
            .hasSize(1)
            .extracting(item -> Tuple.tuple(item.getRate(), item.getInitialDate(), item.getFinalDate()))
            .containsExactly(Tuple.tuple(dto.get(0).rate().floatValue(), dto.get(0).initialDate(), dto.get(0).finalDate()));
    }
}