package com.hss.investment.application.persistence;

import com.hss.investment.application.dto.InvestmentQueryDTO;
import com.hss.investment.application.persistence.entity.Investment;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvestmentRepository extends JpaRepository<Investment, UUID> {

    @Query("""
    SELECT i FROM Investment i
    WHERE (:#{#dto.type} IS NULL OR i.investmentType = :#{#dto.type})
      AND (:#{#dto.bank} IS NULL OR i.bank = :#{#dto.bank})
      AND (i.investmentRange.initialDate <= COALESCE(:#{#dto.initialDate}, i.investmentRange.initialDate))
      AND (i.investmentRange.finalDate >= COALESCE(:#{#dto.finalDate}, i.investmentRange.finalDate))
      AND (:#{#dto.aliquot} IS NULL OR i.baseRate.aliquot = :#{#dto.aliquot})""")
    Page<Investment> findByParameters(@Param("dto") InvestmentQueryDTO dto, Pageable page);

    @Query("SELECT i FROM Investment i WHERE i.completed IS FALSE")
    Page<Investment> findByIncompleted(Pageable page);
}