package com.hss.endpoint;

import com.hss.openapi.api.InvestmentApi;
import com.hss.openapi.model.InvestmentRequestWrapper;
import com.hss.openapi.model.InvestmentResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class InvestmentController implements InvestmentApi {

    @Override
    public ResponseEntity<Void> addInvestments(InvestmentRequestWrapper investmentRequestWrapper) {
        return InvestmentApi.super.addInvestments(investmentRequestWrapper);
    }

    @Override
    public ResponseEntity<InvestmentResultResponse> getInvestments(String type, String bank, LocalDate initialDate, LocalDate finalDate, String aliquot, Integer page, Integer size, String sort) {
        return InvestmentApi.super.getInvestments(type, bank, initialDate, finalDate, aliquot, page, size, sort);
    }
}