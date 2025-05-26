package com.hss.investment.application.service;

import com.hss.investment.application.persistence.IpcaRepository;
import com.hss.investment.application.persistence.SelicRepository;
import com.hss.investment.dto.RateQueryDTO;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public final class RateServiceImpl implements RateService {

    private final SelicRepository selicRepository;
    private final IpcaRepository ipcaRepository;

    @Override
    public List<RateResponseWrapperDataItemsInner> retrieveRates(RateQueryDTO dto) {
        return switch(dto.type()) {
            case SELIC -> selicRepository.findByReferenceDateBetween(dto.initialDate(), dto.finalDate());
            case IPCA -> ipcaRepository.findByReferenceDateBetween(dto.initialDate(), dto.finalDate());
        };
    }
}