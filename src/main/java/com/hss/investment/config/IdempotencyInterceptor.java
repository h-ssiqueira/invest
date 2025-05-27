package com.hss.investment.config;

import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.IdempotencyRepository;
import com.hss.investment.application.persistence.entity.Idempotency;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.MessageDigest;
import java.util.Optional;

import static ch.qos.logback.core.encoder.ByteArrayUtil.toHexString;
import static com.hss.investment.application.exception.ErrorMessages.INV_005;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Component
@Slf4j
public class IdempotencyInterceptor implements HandlerInterceptor {

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(!request.getMethod().equals(POST.name())) {
            return true;
        }
        var idempotency = Optional.ofNullable(request.getHeader("idempotency-Id")).orElse(calculate(request));
        // TODO: set path + method
        var found = idempotencyRepository.findByIdempotencyValue(idempotency);
        if(found.isPresent()) {
            log.warn("Request already executed! id: {}", found.get().getId());
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