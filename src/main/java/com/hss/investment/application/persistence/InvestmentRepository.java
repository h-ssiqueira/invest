package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Investment;
import com.hss.investment.dto.InvestmentQueryDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InvestmentRepository extends JpaRepository<Investment, UUID> {

    @Query("""
            SELECT i FROM Investment i
            WHERE (:#{#dto.type} IS NULL OR :#{#dto.type} = i.investmentType) AND
            (:#{#dto.bank} IS NULL OR :#{#dto.bank} = i.bank) AND
            (:#{#dto.initialDate} IS NULL OR :#{#dto.initialDate} >= i.investmentRange.initialDate) AND
            (:#{#dto.finalDate} IS NULL OR :#{#dto.finalDate} <= i.investmentRange.finalDate) AND
            (:#{#dto.aliquot} IS NULL OR :#{#dto.aliquot} = i.baseRate.aliquot)""")
    List<Investment> findByParameters(@Param("dto") InvestmentQueryDTO dto, Pageable page);
}