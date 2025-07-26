package com.hss.investment.application.persistence;

import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.investment.application.persistence.entity.Selic;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SelicRepository extends JpaRepository<Selic, Integer> {

    @Query("""
        SELECT new com.hss.investment.application.dto.RateQueryResultDTO(s.rate.rate,s.range.initialDate,s.range.finalDate)
        FROM Selic s
        WHERE (s.range.initialDate <= COALESCE(:finalDate, s.range.initialDate))
          AND (s.range.finalDate >= COALESCE(:initialDate, s.range.finalDate))
        ORDER BY s.range.initialDate DESC""")
    List<RateQueryResultDTO> findByReferenceDateBetween(@Param("initialDate") LocalDate initialDate, @Param("finalDate") LocalDate finalDate);

    Optional<Selic> findFirstByOrderByRangeInitialDateDesc();
}