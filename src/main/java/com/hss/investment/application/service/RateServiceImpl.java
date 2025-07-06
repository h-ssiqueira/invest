package com.hss.investment.application.service;

import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.persistence.IpcaRepository;
import com.hss.investment.application.persistence.SelicRepository;
import com.hss.investment.application.persistence.entity.Ipca;
import com.hss.investment.application.persistence.entity.Selic;
import com.hss.investment.application.service.mapper.GeneralMapper;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hss.investment.application.service.validator.DateValidator.validateInitialAndFinalDates;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
@Service
public non-sealed class RateServiceImpl implements RateService {

    private final SelicRepository selicRepository;
    private final IpcaRepository ipcaRepository;
    private final GeneralMapper mapper;

    @Override
    public List<RateResponseWrapperDataItemsInner> retrieveRates(RateQueryDTO dto) {
        validateInitialAndFinalDates(dto.initialDate(), dto.finalDate());
        var response = switch(dto.type()) {
            case SELIC -> selicRepository.findByReferenceDateBetween(dto.initialDate(), dto.finalDate());
            case IPCA -> ipcaRepository.findByReferenceDateBetween(dto.initialDate(), dto.finalDate());
        };
        return mapper.toRateResponseWrapperDataItemsInner(response);
    }

    @Override
    public List<IPCATimeline> getIpcaTimeline(InvestmentRange investmentRange) {
        var list = ipcaRepository.findByReferenceDateBetween(investmentRange.initialDate(), investmentRange.finalDate());

        return list.stream()
            .map(mapper::toIpcaTimeline)
            .toList();
    }

    @Override
    public List<SelicTimeline> getSelicTimeline(InvestmentRange investmentRange) {
        var list = selicRepository.findByReferenceDateBetween(investmentRange.initialDate(), investmentRange.finalDate());

        return list.stream()
            .map(mapper::toSelicTimeline)
            .toList();
    }

    @Override
    public void processIpca(List<Ipca> rateList) {
        var lastIPCAOpt = ipcaRepository.findFirstByOrderByReferenceDateDesc();
        if (lastIPCAOpt.isPresent()) {
            var lastIPCA = lastIPCAOpt.get();
            rateList.removeIf(ipca ->
                ipca.referenceDate().isBefore(lastIPCA.referenceDate()) ||
                ipca.referenceDate().isEqual(lastIPCA.referenceDate())
            );
        }
        log.debug("Saved {} new registers into database", rateList.size());
        ipcaRepository.saveAllAndFlush(rateList);
    }

    @Override
    public void processSelic(List<Selic> rateList) {
        var lastSELICOpt = selicRepository.findFirstByOrderByRangeInitialDateDesc();
        if (lastSELICOpt.isPresent()) {
            var lastSELIC = lastSELICOpt.get();
            var finalDateUpdated = rateList.stream()
                .filter(item -> item.range().initialDate().equals(lastSELIC.range().initialDate()) && nonNull(item.range().finalDate()))
                .findFirst();
            rateList.removeIf(selic ->
                selic.range().initialDate().isBefore(lastSELIC.range().initialDate()) ||
                selic.range().initialDate().isEqual(lastSELIC.range().initialDate())
            );
            if(finalDateUpdated.isPresent()) {
                lastSELIC.range().finalDate(finalDateUpdated.get().range().finalDate());
                rateList.add(lastSELIC);
            }
        }
        log.debug("Saved {} registers into database", rateList.size());
        selicRepository.saveAllAndFlush(rateList);
    }
}