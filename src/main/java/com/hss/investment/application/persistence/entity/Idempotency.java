package com.hss.investment.application.persistence.entity;

import com.hss.investment.application.exception.InvestmentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static com.hss.investment.application.exception.ErrorMessages.INV_001;
import static java.util.Objects.isNull;

@Table(name = "IDEMPOTENCY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Accessors(fluent = true)
public class Idempotency {

    @Id
    @GeneratedValue(generator = "org.hibernate.id.uuid.UuidGenerator")
    @Column(name = "ID", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "IDEMPOTENCY", nullable = false, unique = true, updatable = false)
    private String idempotencyValue;

    @Column(name = "URL", nullable = false, updatable = false)
    private String url;

    @Column(name = "METHOD", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private HttpMethod method;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "RESPONSE")
    private String response;

    private Idempotency(String idempotency) {
        this.idempotencyValue = idempotency;
        this.createdAt = LocalDateTime.now();
    }

    public static Idempotency of(String idempotency) {
        if(isNull(idempotency) || idempotency.isBlank()) {
            throw new InvestmentException(INV_001);
        }
        return new Idempotency(idempotency);
    }

    public void concludeOperation(String json) {
        this.response = json;
    }

    public enum HttpMethod {
        POST,PUT,DELETE,PATCH
    }
}