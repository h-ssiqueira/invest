package com.hss.investment.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice extends ResponseEntityExceptionHandler {

    @SuppressWarnings("unused")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.warn("Handling generic exception: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
            .contentType(APPLICATION_PROBLEM_JSON)
            .body(ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage()));
    }
}