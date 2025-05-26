package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Selic;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SelicRepository  extends JpaRepository<Selic, Integer> {

    @Query("""
        SELECT new com.hss.openapi.model.RateResponseWrapperDataItemsInner().rate(s.rate.rate.floatValue()).initialDate(s.range.initialDate).finalDate(s.range.finalDate)
        FROM Selic s
        WHERE (:initialDate IS NULL AND :finalDate IS NULL) OR
        (:initialDate IS NULL AND s.finalDate <= :finalDate) OR
        (:finalDate IS NULL AND s.initialDate >= :initialDate)
        """)
    List<RateResponseWrapperDataItemsInner> findByReferenceDateBetween(LocalDate initialDate, LocalDate finalDate);
}