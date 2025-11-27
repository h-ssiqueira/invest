package com.hss.investment.config;

import com.hss.investment.application.persistence.IdempotencyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static com.hss.investment.util.InvestmentDTOsMock.getIdempotency;
import static com.hss.investment.util.InvestmentDTOsMock.getInvestmentRequestWrapper;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotencyInterceptorTest {

    private static final String IDEMPOTENCY_HEADER = "idempotency-id";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private IdempotencyRepository idempotencyRepository;

    @InjectMocks
    private IdempotencyInterceptor interceptor;

    @Test
    void preHandleSuccessfully() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/investments");
        when(request.getHeader(any())).thenReturn("POST");
        when(request.getParameterMap()).thenReturn(Map.of());
        when(idempotencyRepository.save(any())).thenReturn(getIdempotency());

        interceptor.preHandle(request, response, getInvestmentRequestWrapper());

        assertAll(
            () -> verify(request, times(2)).getMethod(),
            () -> verify(request, times(2)).getRequestURI(),
            () -> verify(request).getHeader(IDEMPOTENCY_HEADER),
            () -> verify(request).getParameterMap(),
            () -> verify(idempotencyRepository).findByIdempotencyValueAndUrlAndMethod(any(), any(), any()),
            () -> verify(idempotencyRepository).save(any()),
            () -> verify(response).addHeader(eq(IDEMPOTENCY_HEADER), any())
        );
    }

    @Test
    void preHandleSuccessfullyWhenAlreadyExecuted() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/investments");
        when(request.getHeader(any())).thenReturn("POST");
        when(request.getParameterMap()).thenReturn(Map.of());
        when(idempotencyRepository.findByIdempotencyValueAndUrlAndMethod(any(), any(), any())).thenReturn(Optional.of(getIdempotency()));

        interceptor.preHandle(request, response, getInvestmentRequestWrapper());

        assertAll(
            () -> verify(request, times(2)).getMethod(),
            () -> verify(request, times(2)).getRequestURI(),
            () -> verify(request).getHeader(IDEMPOTENCY_HEADER),
            () -> verify(request).getParameterMap(),
            () -> verify(idempotencyRepository).findByIdempotencyValueAndUrlAndMethod(any(), any(), any()),
            () -> verifyNoInteractions(response)
        );
    }

    @Test
    void preHandleWithGetMethod() {
        when(request.getMethod()).thenReturn("GET");

        interceptor.preHandle(request, response, getInvestmentRequestWrapper());

        assertAll(
            () -> verify(request).getMethod(),
            () -> verifyNoInteractions(idempotencyRepository, response)
        );
    }

    @Test
    void preHandleWithUnexpectedAPI() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/rates");

        interceptor.preHandle(request, response, getInvestmentRequestWrapper());

        assertAll(
            () -> verify(request).getMethod(),
            () -> verify(request).getRequestURI(),
            () -> verifyNoInteractions(idempotencyRepository, response)
        );
    }

    @Test
    void postHandleWhenIdDoesNotExist() throws Exception {
        interceptor.postHandle(request, response,getInvestmentRequestWrapper(),null);

        assertAll(
            () -> verify(response).getHeader(IDEMPOTENCY_HEADER),
            () -> verifyNoInteractions(request, idempotencyRepository)
        );
    }

    @Test
    void postHandleSuccessfully() throws Exception {
        var wrapper = mock(ContentCachingResponseWrapper.class);
        when(response.getHeader(IDEMPOTENCY_HEADER)).thenReturn(UUID.randomUUID().toString());
        when(idempotencyRepository.findById(any())).thenReturn(Optional.of(getIdempotency()));
        when(request.getAttribute("responseWrapper")).thenReturn(wrapper);
        when(wrapper.getContentAsByteArray()).thenReturn(new byte[]{45,48,49});
        when(response.getCharacterEncoding()).thenReturn("UTF-8");

        interceptor.postHandle(request, response,getInvestmentRequestWrapper(),null);

        assertAll(
            () -> verify(response).getHeader(IDEMPOTENCY_HEADER),
            () -> verify(idempotencyRepository).findById(any()),
            () -> verify(idempotencyRepository).save(any()),
            () -> verify(request).getAttribute("responseWrapper"),
            () -> verify(wrapper).getContentAsByteArray(),
            () -> verify(response).getCharacterEncoding(),
            () -> verify(wrapper).copyBodyToResponse()
        );
    }

    @Test
    void postHandleWithExceptionWhenCopyingResponse() throws Exception {
        var wrapper = mock(ContentCachingResponseWrapper.class);
        when(response.getHeader(IDEMPOTENCY_HEADER)).thenReturn(UUID.randomUUID().toString());
        when(idempotencyRepository.findById(any())).thenReturn(Optional.of(getIdempotency()));
        when(request.getAttribute("responseWrapper")).thenReturn(wrapper);
        when(wrapper.getContentAsByteArray()).thenReturn(new byte[]{45,48,49});
        when(response.getCharacterEncoding()).thenReturn("UTF-8");
        doThrow(IOException.class).when(wrapper).copyBodyToResponse();

        assertThrows(RuntimeException.class, () -> interceptor.postHandle(request, response,getInvestmentRequestWrapper(),null));

        assertAll(
            () -> verify(response).getHeader(IDEMPOTENCY_HEADER),
            () -> verify(idempotencyRepository).findById(any()),
            () -> verify(idempotencyRepository).save(any()),
            () -> verify(request).getAttribute("responseWrapper"),
            () -> verify(wrapper).getContentAsByteArray(),
            () -> verify(response).getCharacterEncoding(),
            () -> verify(wrapper).copyBodyToResponse()
        );
    }

    @Test
    void postHandleWhenIdempotencyDoesNotExist() throws Exception {
        when(response.getHeader(IDEMPOTENCY_HEADER)).thenReturn(UUID.randomUUID().toString());
        when(idempotencyRepository.findById(any())).thenReturn(Optional.empty());

        interceptor.postHandle(request, response,getInvestmentRequestWrapper(),null);

        assertAll(
            () -> verify(response).getHeader(IDEMPOTENCY_HEADER),
            () -> verify(idempotencyRepository).findById(any()),
            () -> verifyNoInteractions(request)
        );
    }
}