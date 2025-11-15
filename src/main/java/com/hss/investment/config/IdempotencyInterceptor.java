package com.hss.investment.config;

import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.IdempotencyRepository;
import com.hss.investment.application.persistence.entity.Idempotency;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static ch.qos.logback.core.encoder.ByteArrayUtil.toHexString;
import static com.hss.investment.application.exception.ErrorMessages.INV_005;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyInterceptor implements HandlerInterceptor {

    private static final List<String> APIS = List.of("/api/v1/investments");

    private final IdempotencyRepository idempotencyRepository;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        try {
            Idempotency.HttpMethod.valueOf(request.getMethod());
        } catch (IllegalArgumentException ex) {
            return true;
        }
        if(APIS.contains(request.getRequestURI())) {
            var idempotency = Optional.ofNullable(request.getHeader("idempotency-Id")).orElse(calculate(request));
            var found = idempotencyRepository.findByIdempotencyValueAndUrlAndMethod(idempotency, request.getRequestURI(), Idempotency.HttpMethod.valueOf(request.getMethod()));
            if (found.isPresent()) {
                log.warn("Request already executed! id: {}", found.get().id());
                return false;
            }
            idempotencyRepository.save(Idempotency.of(idempotency));
        }
        return true;
    }

    private String calculate(HttpServletRequest request) {
        try {
            var sha256 = MessageDigest.getInstance("SHA-256");
            var params = request.getParameterMap();
            return toHexString(sha256.digest(params.toString().getBytes()));
        } catch(Exception e) {
            throw new InvestmentException(INV_005.formatted(e.getMessage()));
        }
    }
}