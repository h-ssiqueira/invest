package com.hss.application.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Table(name = "IPCA")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Ipca {

    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false)
    private Integer id;

    @Column(name = "REFERENCE_DATE", unique = true, updatable = false, nullable = false)
    private LocalDate referenceDate;

    @Embedded
    private Percentage rate;
}