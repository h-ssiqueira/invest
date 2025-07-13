package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    @Query("""
        SELECT DISTINCT referenceDate FROM Holiday
        WHERE referenceDate BETWEEN :initialDate AND :finalDate
        ORDER BY referenceDate DESC;""")
    List<LocalDate> findByReferenceDateBetween(@Param("initialDate") LocalDate initialDate, @Param("finalDate") LocalDate finalDate);
}