package com.hss.investment.application.service;

import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.persistence.IpcaRepository;
import com.hss.investment.application.persistence.SelicRepository;
import com.hss.investment.application.service.mapper.GeneralMapper;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hss.investment.application.service.validator.DateValidator.validateInitialAndFinalDates;

@RequiredArgsConstructor
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
}