package com.hss.investment.endpoint;

import com.hss.investment.application.dto.GenericResponseDTO;
import com.hss.investment.application.dto.InvestmentQueryDTO;
import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.entity.Investment;
import com.hss.investment.application.service.InvestmentService;
import com.hss.openapi.api.InvestmentApi;
import com.hss.openapi.model.InvestmentErrorResponseDTO;
import com.hss.openapi.model.InvestmentRequestWrapper;
import com.hss.openapi.model.InvestmentResultResponseData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.hss.investment.application.dto.utils.SortExtractor.extractSort;
import static com.hss.investment.application.exception.ErrorMessages.INV_004;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.MULTI_STATUS;

@RestController
@RequiredArgsConstructor
public class InvestmentController implements InvestmentApi {

    private final InvestmentService investmentService;

    @Override
    public ResponseEntity<GenericResponseDTO<?>> addInvestments(HttpServletRequest request, HttpServletResponse response, InvestmentRequestWrapper investmentRequestWrapper) {
        var responseDto = investmentService.addInvestments(investmentRequestWrapper.getItems());
        var errors = responseDto.getItems()
            .stream()
            .filter(InvestmentErrorResponseDTO.class::isInstance)
            .count();
        if(errors == investmentRequestWrapper.getItems().size()) {
            throw new InvestmentException(INV_004);
        }
        return errors == 0L ? ResponseEntity.status(CREATED).build() : ResponseEntity.status(MULTI_STATUS).body(new GenericResponseDTO<>(responseDto));
    }

    @Override
    public ResponseEntity<GenericResponseDTO<?>> getInvestments(HttpServletRequest request, HttpServletResponse response, String type, String bank, LocalDate initialDate, LocalDate finalDate, String aliquot, Integer page, Integer size, String sort) {
        var result = investmentService.retrieveInvestments(new InvestmentQueryDTO(
            Investment.InvestmentType.valueOf(type),
            bank,
            initialDate, finalDate,
            Investment.AliquotType.valueOf(aliquot),
            PageRequest.of(page, size, extractSort(sort)))
        );
        return ResponseEntity.ok(new GenericResponseDTO<>(new InvestmentResultResponseData().items(result)));
    }
}