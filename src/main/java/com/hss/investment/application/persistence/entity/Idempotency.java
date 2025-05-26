package com.hss.investment.application.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@Table(name = "IDEMPOTENCY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Idempotency {

    @Id
    @GeneratedValue(generator = "org.hibernate.id.uuid.UuidGenerator")
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "IDEMPOTENCY", nullable = false, unique = true, updatable = false)
    private String idempotencyValue;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Idempotency(String idempotency) {
        this.idempotencyValue = idempotency;
        this.createdAt = LocalDateTime.now();
    }

    public static Idempotency of(String idempotency) {
        if(isNull(idempotency) || idempotency.isBlank()) {
            throw new IllegalArgumentException();
        }
        return new Idempotency(idempotency);
    }
}