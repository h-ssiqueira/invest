package com.hss.investment.config;

import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.IdempotencyRepository;
import com.hss.investment.application.persistence.entity.Idempotency;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static ch.qos.logback.core.encoder.ByteArrayUtil.toHexString;
import static com.hss.investment.application.exception.ErrorMessages.INV_005;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyInterceptor implements HandlerInterceptor {

    private static final String IDEMPOTENCY_HEADER = "idempotency-id";
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
            var idempotency = Optional.ofNullable(request.getHeader(IDEMPOTENCY_HEADER)).orElse(calculate(request));
            var found = idempotencyRepository.findByIdempotencyValueAndUrlAndMethod(idempotency, request.getRequestURI(), Idempotency.HttpMethod.valueOf(request.getMethod()));
            if (found.isPresent()) {
                log.warn("Request already executed! id: {}", found.get().id());
                return false;
            }
            var idempotencySaved = idempotencyRepository.save(Idempotency.of(idempotency));
            response.addHeader(IDEMPOTENCY_HEADER, String.valueOf(idempotencySaved.id()));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        var id = response.getHeader(IDEMPOTENCY_HEADER);
        if (id == null) {
            return;
        }
        idempotencyRepository.findById(UUID.fromString(id)).ifPresentOrElse(
            item -> {
                try {
                    var wrapper = (ContentCachingResponseWrapper) request.getAttribute("responseWrapper");
                    var json = wrapper.getContentAsByteArray();
                    item.concludeOperation(new String(json, response.getCharacterEncoding()));
                    idempotencyRepository.save(item);
                    wrapper.copyBodyToResponse();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            },
            () -> log.warn("Idempotency was not found")
        );
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