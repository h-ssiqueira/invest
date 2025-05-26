package com.hss.investment.application.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "HOLIDAY")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Holiday {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "REFERENCE_DATE", nullable = false, updatable = false, unique = true)
    private LocalDate referenceDate;

    @Column(name = "NAME", nullable = false, updatable = false)
    private String name;
}