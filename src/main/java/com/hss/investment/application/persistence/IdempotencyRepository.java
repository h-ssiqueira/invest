package com.hss.investment.application.persistence;

import com.hss.investment.application.persistence.entity.Idempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRepository extends JpaRepository<Idempotency, UUID> {

    Optional<Idempotency> findByIdempotency(String idempotency);
}