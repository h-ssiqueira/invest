package com.hss.investment.util;

import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.investment.application.dto.calculation.IPCATimeline;
import com.hss.investment.application.dto.calculation.InvestmentCalculationIPCA;
import com.hss.investment.application.dto.calculation.InvestmentCalculationSelic;
import com.hss.investment.application.dto.calculation.InvestmentCalculationSimple;
import com.hss.investment.application.dto.calculation.SelicTimeline;
import com.hss.investment.application.persistence.entity.Investment;
import com.hss.investment.application.persistence.entity.Ipca;
import com.hss.investment.application.persistence.entity.Selic;
import com.hss.openapi.model.InvestmentAliquot;
import com.hss.openapi.model.InvestmentErrorResponseDTO;
import com.hss.openapi.model.InvestmentErrorResponseDTOErrorsInner;
import com.hss.openapi.model.InvestmentRequest;
import com.hss.openapi.model.InvestmentRequestWrapper;
import com.hss.openapi.model.InvestmentSimulationResultResponseDTO;
import com.hss.openapi.model.InvestmentType;
import com.hss.openapi.model.PartialInvestmentResultData;
import com.hss.openapi.model.SimulationInvestmentRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.provider.Arguments;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvestmentDTOsMock {

    public static List<RateQueryResultDTO> getRateQueryResultDTOList() {
        return List.of(new RateQueryResultDTO(BigDecimal.ONE, LocalDate.now(), LocalDate.now().plusDays(1L)));
    }

    public static List<InvestmentRequest> getInvestmentRequestList() {
        return List.of(getInvestmentRequest(),
            new InvestmentRequest()
                .rate(0.01D)
                .type(InvestmentType.LCA)
                .bank("National")
                .aliquot(InvestmentAliquot.POSTFIXED)
                .amount(1000.)
                .initialDate(LocalDate.of(2020,1,1))
                .finalDate(LocalDate.of(2015,1,1)));
    }

    public static InvestmentRequestWrapper getInvestmentRequestWrapper() {
        return new InvestmentRequestWrapper().items(getInvestmentRequestList());
    }

    public static InvestmentRequestWrapper getInvestmentRequestWrapperError() {
        return new InvestmentRequestWrapper().items(List.of(getInvestmentRequest()));
    }

    public static InvestmentRequest getInvestmentRequest() {
        return new InvestmentRequest()
            .rate(0.1D)
            .type(InvestmentType.CDB)
            .bank("National")
            .aliquot(InvestmentAliquot.PREFIXED)
            .amount(1000.)
            .initialDate(LocalDate.of(2020,1,1))
            .finalDate(LocalDate.of(2025,1,1));
    }

    public static InvestmentErrorResponseDTO getInvestmentErrorResponseDTO() {
        return new InvestmentErrorResponseDTO()
            .type("type").title("title")
            .errors(List.of(new InvestmentErrorResponseDTOErrorsInner().detail("Invalid date").pointer("pointer")));
    }

    public static PartialInvestmentResultData getPartialInvestmentResultDataSuccess() {
        return new PartialInvestmentResultData().items(List.of(getInvestmentRequest()));
    }

    public static InvestmentSimulationResultResponseDTO getInvestmentSimulationResultResponseDTO() {
        return new InvestmentSimulationResultResponseDTO()
            .type(InvestmentType.LCA)
            .rate(97.5D)
            .aliquot(InvestmentAliquot.POSTFIXED)
            .initialDate(LocalDate.of(2020,3,13))
            .finalDate(LocalDate.of(2022,2,4))
            .amount(10000.5D)
            .earnings(15000D)
            .profit(5000D);
    }

    public static SimulationInvestmentRequest getSimulationInvestmentRequest() {
        return new SimulationInvestmentRequest()
            .type(InvestmentType.LCA)
            .aliquot(InvestmentAliquot.POSTFIXED)
            .rate(97D)
            .initialDate(LocalDate.of(2020,3,13))
            .finalDate(LocalDate.of(2022,2,4))
            .amount(10000.5D);
    }

    public static SimulationInvestmentRequest getSimulationInvestmentRequestError() {
        return new SimulationInvestmentRequest()
            .type(InvestmentType.LCA)
            .aliquot(InvestmentAliquot.POSTFIXED)
            .rate(97D)
            .finalDate(LocalDate.of(2020,3,13))
            .initialDate(LocalDate.of(2022,2,4))
            .amount(10000.5D);
    }

    public static Stream<Arguments> getCalculationTypes() {
        return Stream.of(
            Arguments.of(InvestmentCalculationSimple.builder()
                    .type(Investment.InvestmentType.CDB)
                    .investmentRange(Investment.InvestmentRange.of(LocalDate.of(2000,5,5),LocalDate.of(2020,10,25)))
                    .amount(BigDecimal.valueOf(1000))
                    .rate(BigDecimal.TEN)
                .build()),
            Arguments.of(InvestmentCalculationIPCA.builder()
                .type(Investment.InvestmentType.LCA)
                .investmentRange(Investment.InvestmentRange.of(LocalDate.of(2020,5,5),LocalDate.of(2020,10,25)))
                .amount(BigDecimal.valueOf(500))
                .rate(BigDecimal.valueOf(13.55D))
                .ipcaTimeline(List.of(IPCATimeline.builder().rate(BigDecimal.valueOf(0.5D)).month(YearMonth.of(2020,3)).build(),
                    IPCATimeline.builder().rate(BigDecimal.ONE).month(YearMonth.of(2020,4)).build(),
                    IPCATimeline.builder().rate(BigDecimal.valueOf(2)).month(YearMonth.of(2020,5)).build(),
                    IPCATimeline.builder().rate(BigDecimal.valueOf(3)).month(YearMonth.of(2020,6)).build(),
                    IPCATimeline.builder().rate(BigDecimal.valueOf(4)).month(YearMonth.of(2020,7)).build()))
                .build()),
            Arguments.of(InvestmentCalculationSelic.builder()
                .type(Investment.InvestmentType.CRI)
                .investmentRange(Investment.InvestmentRange.of(LocalDate.of(2020,5,5),LocalDate.of(2030,10,25)))
                .amount(BigDecimal.valueOf(500))
                .rate(BigDecimal.valueOf(13.55D))
                    .selicTimeline(List.of(
                        SelicTimeline.builder().rate(BigDecimal.valueOf(12.5)).investmentRange(Investment.InvestmentRange.of(LocalDate.of(2018,8,22),LocalDate.of(2022,10,25))).build(),
                        SelicTimeline.builder().rate(BigDecimal.TEN).investmentRange(Investment.InvestmentRange.of(LocalDate.of(2022,10,26),LocalDate.of(2028,12,25))).build(),
                        SelicTimeline.builder().rate(BigDecimal.valueOf(8D)).investmentRange(Investment.InvestmentRange.of(LocalDate.of(2028,12,26),LocalDate.of(2035,10,25))).build()
                    ))
                .build())
        );
    }

    public static PartialInvestmentResultData getPartialInvestmentResultDataError() {
        return new PartialInvestmentResultData().items(List.of(getInvestmentErrorResponseDTO()));
    }

    public static PartialInvestmentResultData getPartialInvestmentResultDataMultiStatus() {
        return new PartialInvestmentResultData().items(List.of(
            getInvestmentRequest(),
            getInvestmentErrorResponseDTO()
        ));
    }

    public static Ipca getIpca() {
        return Ipca.of(LocalDate.of(2000,8,31),BigDecimal.TEN);
    }

    public static Selic getSelic() {
        return Selic.of(LocalDate.of(2000,8,31),LocalDate.of(2022,2,5),BigDecimal.ONE);
    }

    public static Selic getSelicWithoutfinalDate() {
        return Selic.of(LocalDate.of(2000,8,31),null,BigDecimal.ONE);
    }

    public static Stream<Arguments> getProcessingSelicLists() {
        var same = new ArrayList<Selic>();
        var selic = getSelic();
        same.add(selic);
        same.add(selic);
        var nextSelic = getSelicWithoutfinalDate();
        var selics = new ArrayList<Selic>();
        selics.add(nextSelic);
        selics.add(selic);
        return Stream.of(
            Arguments.of(same),
            Arguments.of(selics)
        );
    }

    public static Stream<Arguments> getLastUpdates() {
        return Stream.of(
            Arguments.of(Optional.empty()),
            Arguments.of(Optional.of(ZonedDateTime.now().minusDays(1)))
        );
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