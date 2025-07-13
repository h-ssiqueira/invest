package com.hss.investment.endpoint;

import com.hss.investment.application.dto.GenericResponseDTO;
import com.hss.investment.application.dto.RateQueryDTO;
import com.hss.investment.application.service.RateKaggleUpdater;
import com.hss.investment.application.service.RateService;
import com.hss.openapi.api.RateApi;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RateController implements RateApi {

    private final RateService rateService;
    private final RateKaggleUpdater rateKaggleUpdater;

    @Override
    public ResponseEntity<GenericResponseDTO<?>> getRate(HttpServletRequest request,
                                                         HttpServletResponse response,
                                                         String rateType,
                                                         LocalDate initialDate,
                                                         LocalDate finalDate) {
        var responseDto = rateService.retrieveRates(new RateQueryDTO(
            RateQueryDTO.RateType.fromValue(rateType), initialDate, finalDate
        ));
        return ResponseEntity.ok()
            .headers(buildHeader())
            .body(new GenericResponseDTO<>(responseDto));
    }

    @Override
    public ResponseEntity<GenericResponseDTO<?>> updateRates(HttpServletRequest request,
                                                             HttpServletResponse response) {
        rateKaggleUpdater.retrieveAndUpdateRates();
        return ResponseEntity.ok()
            .headers(buildHeader())
            .body(new GenericResponseDTO<>(null));
    }

    private HttpHeaders buildHeader() {
        var headers = new HttpHeaders();
        headers.add("X-Last-Update",rateKaggleUpdater.retrieveLastUpdateTimestamp()
            .map(Object::toString)
            .orElse(null));
        return headers;
    }
}