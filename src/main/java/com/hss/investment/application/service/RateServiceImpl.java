package com.hss.investment.application.service;

import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.persistence.IpcaRepository;
import com.hss.investment.application.persistence.SelicRepository;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public non-sealed class RateServiceImpl implements RateService {

    private final SelicRepository selicRepository;
    private final IpcaRepository ipcaRepository;

    @Override
    public List<RateResponseWrapperDataItemsInner> retrieveRates(RateQueryDTO dto) {
        var response = switch(dto.type()) {
            case SELIC -> selicRepository.findByReferenceDateBetween(dto.initialDate(), dto.finalDate());
            case IPCA -> ipcaRepository.findByReferenceDateBetween(dto.initialDate(), dto.finalDate());
        };
        return response.stream()
            .map(item -> new RateResponseWrapperDataItemsInner()
                .rate(item.getRate().floatValue())
                .initialDate(item.getInitialDate())
                .finalDate(item.getFinalDate()))
            .toList();
    }
}