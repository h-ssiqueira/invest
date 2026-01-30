package com.hss.investment.application.service;

import com.hss.investment.application.dto.InvestmentQueryDTO;
import com.hss.investment.application.dto.calculation.InvestmentCalculationBase;
import com.hss.investment.application.dto.calculation.InvestmentCalculationIPCA;
import com.hss.investment.application.dto.calculation.InvestmentCalculationSelic;
import com.hss.investment.application.dto.calculation.InvestmentCalculationSimple;
import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.ConfigurationDao;
import com.hss.investment.application.persistence.InvestmentRepository;
import com.hss.investment.application.persistence.entity.Investment;
import com.hss.investment.application.service.calculation.InvestmentCalculationDelegator;
import com.hss.openapi.model.InvestmentAliquot;
import com.hss.openapi.model.InvestmentErrorResponseDTO;
import com.hss.openapi.model.InvestmentRequest;
import com.hss.openapi.model.InvestmentResultResponseDTO;
import com.hss.openapi.model.InvestmentSimulationResultResponseDTO;
import com.hss.openapi.model.InvestmentType;
import com.hss.openapi.model.PartialInvestmentResultData;
import com.hss.openapi.model.PartialInvestmentResultDataItemsInner;
import com.hss.openapi.model.SimulationInvestmentRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static com.hss.investment.application.exception.ErrorMessages.INV_006;
import static com.hss.investment.application.exception.ErrorMessages.INV_007;
import static com.hss.investment.application.service.validator.DateValidator.validateInitialAndFinalDates;

@Service
@Slf4j
@RequiredArgsConstructor
public non-sealed class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final InvestmentCalculationDelegator<InvestmentCalculationBase> delegator;
    private final RateService rateService;
    private final ConfigurationDao configurationDao;

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
                    Investment.BaseRate.of(
                        Investment.AliquotType.valueOf(investment.getAliquot().getValue()),
                        BigDecimal.valueOf(investment.getRate())),
                    BigDecimal.valueOf(investment.getAmount())
                );
                items.add(entity);
                responseList.add(investment);
            } catch (InvestmentException ex) {
                responseList.add(new InvestmentErrorResponseDTO().type(ex.getClass().getName()).title(ex.getMessage()));
            }
        });
        investmentRepository.saveAll(items);
        return new PartialInvestmentResultData().items(responseList);
    }

    @Override
    public Page<InvestmentResultResponseDTO> retrieveInvestments(InvestmentQueryDTO dto) {
        validateInitialAndFinalDates(dto.initialDate(), dto.finalDate());
        var result = investmentRepository.findByParameters(dto, dto.page());
        return new PageImpl<>(
        result.getContent().stream()
            .map(item -> {
                var calculations = delegator.delegate(retrieveDTO(item));
                return new InvestmentResultResponseDTO()
                    .bank(item.bank())
                    .amount(item.amountFormatted())
                    .initialDate(item.investmentRange().initialDate())
                    .finalDate(item.investmentRange().finalDate())
                    .tax(item.getTaxFormatted())
                    .type(InvestmentType.valueOf(item.investmentType().name()))
                    .rate(item.baseRate().rate().ratePercentage())
                    .profit(calculations.profitFormatted())
                    .aliquot(InvestmentAliquot.fromValue(item.baseRate().aliquot().name()))
                    .earnings(calculations.earningsFormatted());
            }).toList(), result.getPageable(), result.getTotalElements());
    }

    @Override
    public InvestmentSimulationResultResponseDTO simulateInvestment(SimulationInvestmentRequest dto) {
        validateInitialAndFinalDates(dto.getInitialDate(), dto.getFinalDate());
        var calculations = delegator.delegate(retrieveDTO(dto));
        return new InvestmentSimulationResultResponseDTO()
            .amount(dto.getAmount())
            .initialDate(dto.getInitialDate())
            .finalDate(dto.getFinalDate())
            .tax(Investment.InvestmentType.valueOf(dto.getType().name()).hasTaxes() ?
                Investment.InvestmentRange.of(dto.getInitialDate(), dto.getFinalDate()).getTaxFormatted() :
                null)
            .type(dto.getType())
            .rate(dto.getRate())
            .aliquot(dto.getAliquot())
            .profit(calculations.profitFormatted())
            .earnings(calculations.earningsFormatted());
    }

    @Override
    public void completeInvestment(UUID id) {
        var investment = investmentRepository.findById(id).orElseThrow(() -> new InvestmentException(INV_006.formatted("Investment")));
        if (investment.investmentRange().isCompleted()) {
            throw new InvestmentException(INV_007);
        }
        completeInvestment(investment);
    }

    @Override
    public void updateInvestments() {
        var lastDate = configurationDao.getLastInvestmentUpdated();
        if (lastDate.isPresent() && lastDate.get().equals(LocalDate.now())) {
            return;
        }
        log.info("Finding completed investments...");
        var page = PageRequest.of(0,20);
        var list = investmentRepository.findByIncompleted(page);
        log.info("{} completed investments found", list.getTotalElements());
        while (list.hasContent()) {
            list.forEach(this::completeInvestment);
            page = page.next();
            list = investmentRepository.findByIncompleted(page);
        }
        configurationDao.saveLastInvestmentUpdate(LocalDate.now());
    }

    private void completeInvestment(Investment investment) {
        var calculationResult = delegator.delegate(retrieveDTO(investment));
        investment.terminateInvestment(Investment.ProfitResult.of(calculationResult.earnings(), calculationResult.profit()));
        investmentRepository.save(investment);
    }

    private InvestmentCalculationBase retrieveDTO(SimulationInvestmentRequest item) {
        var range = Investment.InvestmentRange.of(item.getInitialDate(), item.getFinalDate());
        var type = Investment.InvestmentType.valueOf(item.getType().name());
        var rate = BigDecimal.valueOf(item.getRate()).divide(BigDecimal.valueOf(100L), 10, RoundingMode.HALF_EVEN);
        return switch (item.getAliquot()) {
            case PREFIXED -> InvestmentCalculationSimple.builder()
                .type(type)
                .rate(rate)
                .amount(BigDecimal.valueOf(item.getAmount()))
                .investmentRange(range).build();
            case POSTFIXED -> InvestmentCalculationSelic.builder()
                .type(type)
                .selicTimeline(rateService.getSelicTimeline(range))
                .rate(rate)
                .amount(BigDecimal.valueOf(item.getAmount()))
                .investmentRange(range).build();
            case INFLATION -> InvestmentCalculationIPCA.builder()
                .type(type)
                .ipcaTimeline(rateService.getIpcaTimeline(range))
                .rate(rate)
                .amount(BigDecimal.valueOf(item.getAmount()))
                .investmentRange(range).build();
        };
    }

    private InvestmentCalculationBase retrieveDTO(Investment item) {
        var rate = item.baseRate().rate().rateCalculate();
        return switch (item.baseRate().aliquot()) {
            case PREFIXED -> InvestmentCalculationSimple.builder()
                .type(item.investmentType())
                .rate(rate)
                .amount(item.amount())
                .investmentRange(item.investmentRange()).build();
            case POSTFIXED -> InvestmentCalculationSelic.builder()
                .type(item.investmentType())
                .selicTimeline(rateService.getSelicTimeline(item.investmentRange()))
                .rate(rate)
                .amount(item.amount())
                .investmentRange(item.investmentRange()).build();
            case INFLATION -> InvestmentCalculationIPCA.builder()
                .type(item.investmentType())
                .ipcaTimeline(rateService.getIpcaTimeline(item.investmentRange()))
                .rate(rate)
                .amount(item.amount())
                .investmentRange(item.investmentRange()).build();
        };
    }
}