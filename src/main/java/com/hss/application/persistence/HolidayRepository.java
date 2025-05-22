package com.hss.application.persistence;

import com.hss.application.persistence.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    List<Holiday> findByReferenceDateBetween(LocalDate initialDate, LocalDate finalDate);
}