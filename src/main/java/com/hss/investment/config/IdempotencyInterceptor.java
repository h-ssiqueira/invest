package com.hss.investment.config;

import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.IdempotencyRepository;
import com.hss.investment.application.persistence.entity.Idempotency;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Optional;

import static ch.qos.logback.core.encoder.ByteArrayUtil.toHexString;
import static com.hss.investment.application.exception.ErrorMessages.INV_005;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final IdempotencyRepository idempotencyRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(Arrays.stream(Idempotency.HttpMethod.values()).noneMatch(x -> x.equals(request.getMethod()))) {
            return true;
        }
        var idempotency = Optional.ofNullable(request.getHeader("idempotency-Id")).orElse(calculate(request));
        var found = idempotencyRepository.findByIdempotencyValueAndUrlAndMethod(idempotency, request.getRequestURI(), Idempotency.HttpMethod.valueOf(request.getMethod()));
        if(found.isPresent()) {
            log.warn("Request already executed! id: {}", found.get().id());
            return false;
        }
        idempotencyRepository.save(Idempotency.of(idempotency));
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