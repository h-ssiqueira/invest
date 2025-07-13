package com.hss.investment.application.dto;

import com.hss.investment.application.persistence.entity.Investment;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

public record InvestmentQueryDTO(Investment.InvestmentType type,
                                 String bank,
                                 LocalDate initialDate,
                                 LocalDate finalDate,
                                 Investment.AliquotType aliquot,
                                 Pageable page) {
}