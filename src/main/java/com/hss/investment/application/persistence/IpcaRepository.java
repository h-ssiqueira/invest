package com.hss.investment.application.persistence;

import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.investment.application.persistence.entity.Ipca;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IpcaRepository extends JpaRepository<Ipca, Integer> {

    @Query("""
        SELECT new com.hss.investment.application.dto.RateQueryResultDTO(i.rate.rate,i.referenceDate)
        FROM Ipca i
        WHERE (i.referenceDate BETWEEN COALESCE(:initialDate, i.referenceDate) AND COALESCE(:finalDate, i.referenceDate))
        ORDER BY i.referenceDate ASC""")
    List<RateQueryResultDTO> findByReferenceDateBetween(@Param("initialDate") LocalDate initialDate, @Param("finalDate") LocalDate finalDate);

    Optional<Ipca> findFirstByOrderByReferenceDateDesc();
}