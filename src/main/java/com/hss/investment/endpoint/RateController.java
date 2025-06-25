package com.hss.investment.endpoint;

import com.hss.investment.application.dto.GenericResponseDTO;
import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.service.RateService;
import com.hss.openapi.api.RateApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class RateController implements RateApi {

    private final RateService rateService;

    @Override
    public ResponseEntity<GenericResponseDTO<?>> getRate(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         String rateType,
                                                         LocalDate initialDate,
                                                         LocalDate finalDate) {
        var responseDto = rateService.retrieveRates(new RateQueryDTO(
            RateQueryDTO.RateType.fromValue(rateType), initialDate, finalDate
        ));
        return ResponseEntity.ok(new GenericResponseDTO<>(responseDto));
    }


}