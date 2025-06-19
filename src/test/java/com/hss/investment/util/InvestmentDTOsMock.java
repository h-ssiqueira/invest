package com.hss.investment.util;

import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.investment.application.persistence.entity.Investment;
import com.hss.openapi.model.InvestmentAliquot;
import com.hss.openapi.model.InvestmentErrorResponseDTO;
import com.hss.openapi.model.InvestmentErrorResponseDTOErrorsInner;
import com.hss.openapi.model.InvestmentRequest;
import com.hss.openapi.model.InvestmentRequestWrapper;
import com.hss.openapi.model.InvestmentType;
import com.hss.openapi.model.PartialInvestmentResultData;
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

    public static List<InvestmentRequest> getInvestmentRequestList() {
        return List.of(getInvestmentRequest(),
            new InvestmentRequest()
                .rate(0.01F)
                .type(InvestmentType.LCA)
                .bank("National")
                .aliquot(InvestmentAliquot.POSTFIXED)
                .amount(1000.)
                .initialDate(LocalDate.of(2020,1,1))
                .finalDate(LocalDate.of(2015,1,1)));
    }

    public static InvestmentRequestWrapper getInvestmentRequestWrapper() {
        return new InvestmentRequestWrapper()
            .items(getInvestmentRequestList());
    }

    public static InvestmentRequestWrapper getInvestmentRequestWrapperError() {
        return new InvestmentRequestWrapper()
            .addItemsItem(getInvestmentRequest());
    }

    public static InvestmentRequest getInvestmentRequest() {
        return new InvestmentRequest()
            .rate(0.1F)
            .type(InvestmentType.CDB)
            .bank("National")
            .aliquot(InvestmentAliquot.PREFIXED)
            .amount(1000.)
            .initialDate(LocalDate.of(2020,1,1))
            .finalDate(LocalDate.of(2025,1,1));
    }

    public static InvestmentErrorResponseDTO getInvestmentErrorResponseDTO() {
        return new InvestmentErrorResponseDTO()
            .addErrorsItem(new InvestmentErrorResponseDTOErrorsInner().detail("Invalid date"));
    }

    public static PartialInvestmentResultData getPartialInvestmentResultDataSuccess() {
        return new PartialInvestmentResultData().addItemsItem(getInvestmentRequest());
    }

    public static PartialInvestmentResultData getPartialInvestmentResultDataError() {
        return new PartialInvestmentResultData()
            .addItemsItem(getInvestmentErrorResponseDTO());
    }

    public static PartialInvestmentResultData getPartialInvestmentResultDataMultiStatus() {
        return new PartialInvestmentResultData().items(List.of(
            getInvestmentRequest(),
            getInvestmentErrorResponseDTO()
        ));
    }

    public static RateQueryDTO getIpcaQueryDTO() {
        return new RateQueryDTO(RateQueryDTO.RateType.IPCA, LocalDate.of(2020,3,13), LocalDate.of(2022,1,1));
    }

    public static RateQueryDTO getSelicQueryDTO() {
        return new RateQueryDTO(RateQueryDTO.RateType.SELIC, LocalDate.of(2000,8,31), LocalDate.of(2022,1,24));
    }

    public static List<RateQueryResultDTO> getRateQueryResultList() {
        return List.of(new RateQueryResultDTO(BigDecimal.ONE, LocalDate.of(2025,8,31),null));
    }
}