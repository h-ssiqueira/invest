package com.hss.endpoint;

import com.hss.openapi.api.RateApi;
import com.hss.openapi.model.RateResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class RateController implements RateApi {

    @Override
    public ResponseEntity<RateResponseWrapper> getRate(String rateType, LocalDate initialDate, LocalDate finalDate) {
        return RateApi.super.getRate(rateType, initialDate, finalDate);
    }
}