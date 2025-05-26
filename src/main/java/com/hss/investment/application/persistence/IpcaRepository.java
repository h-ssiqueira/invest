package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Ipca;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface IpcaRepository extends JpaRepository<Ipca, Integer> {

    @Query("""
        SELECT new com.hss.openapi.model.RateResponseWrapperDataItemsInner().rate(i.rate.rate.floatValue()).initialDate(i.referenceDate)
        FROM Ipca i
        WHERE (i.referenceDate BETWEEN :initialDate AND :finalDate) OR
        (:initialDate IS NULL AND :finalDate IS NULL) OR
        (:initialDate IS NULL AND i.referenceDate <= :finalDate) OR
        (:finalDate IS NULL AND i.referenceDate >= :initialDate)
        """)
    List<RateResponseWrapperDataItemsInner> findByReferenceDateBetween(LocalDate initialDate, LocalDate finalDate);
}