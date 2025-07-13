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
        SELECT new com.hss.investment.application.dto.RateQueryResultDTO(i.rate.rate,i.referenceDate,null)
        FROM Ipca i
        WHERE (i.referenceDate BETWEEN :initialDate AND :finalDate) OR
        (:initialDate IS NULL AND :finalDate IS NULL) OR
        (:initialDate IS NULL AND i.referenceDate <= :finalDate) OR
        (:finalDate IS NULL AND i.referenceDate >= :initialDate)
        ORDER BY i.referenceDate DESC""")
    List<RateQueryResultDTO> findByReferenceDateBetween(@Param("initialDate") LocalDate initialDate, @Param("finalDate") LocalDate finalDate);

    Optional<Ipca> findFirstByOrderByReferenceDateDesc();
}