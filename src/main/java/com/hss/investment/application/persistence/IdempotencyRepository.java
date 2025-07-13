package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Idempotency;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<Idempotency, UUID> {

    Optional<Idempotency> findByIdempotencyValueAndUrlAndMethod(String idempotency, String url, Idempotency.HttpMethod method);
}