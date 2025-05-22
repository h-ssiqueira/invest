package com.hss.application.persistence.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Table(name = "SELIC")
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Selic {

    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false)
    private Integer id;

    @AttributeOverride(name = "initialDate", column = @Column(name = "REFERENCE_START_DATE", unique = true, updatable = false, nullable = false))
    @AttributeOverride(name = "finalDate", column = @Column(name = "REFERENCE_END_DATE", unique = true, updatable = false))
    private Investment.InvestmentRange range;

    @Embedded
    private Percentage rate;
}