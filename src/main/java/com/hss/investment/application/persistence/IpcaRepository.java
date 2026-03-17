package com.hss.investment.application.persistence;

import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.investment.application.persistence.entity.Ipca;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IpcaRepository extends JpaRepository<Ipca, Integer>, IpcaCustomRepository {

    @Query("""
        SELECT new com.hss.investment.application.dto.RateQueryResultDTO(i.rate.rate,i.referenceDate)
        FROM Ipca i
        WHERE date_trunc('month', i.referenceDate) BETWEEN
              COALESCE(date_trunc('month', CAST(:initialDate AS timestamp)),
                       date_trunc('month', i.referenceDate)) AND
              COALESCE(date_trunc('month', CAST(:finalDate AS timestamp)),
                       date_trunc('month', i.referenceDate))
        ORDER BY i.referenceDate ASC""")
    List<RateQueryResultDTO> findByReferenceDateBetween(@Param("initialDate") LocalDate initialDate, @Param("finalDate") LocalDate finalDate);

    Optional<Ipca> findFirstByOrderByReferenceDateDesc();
}