package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    List<Holiday> findByReferenceDateBetween(LocalDate initialDate, LocalDate finalDate);
}