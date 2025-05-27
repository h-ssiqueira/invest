package com.hss.investment.application.persistence;

import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.investment.application.persistence.entity.Selic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SelicRepository extends JpaRepository<Selic, Integer> {

    @Query("""
        SELECT new com.hss.investment.application.dto.RateQueryResultDTO(s.rate.rate,s.range.initialDate,s.range.finalDate)
        FROM Selic s
        WHERE (:initialDate IS NULL AND :finalDate IS NULL) OR
        (:initialDate IS NULL AND s.range.finalDate <= :finalDate) OR
        (:finalDate IS NULL AND s.range.initialDate >= :initialDate)
        """)
    List<RateQueryResultDTO> findByReferenceDateBetween(@Param("initialDate") LocalDate initialDate, @Param("finalDate") LocalDate finalDate);
}