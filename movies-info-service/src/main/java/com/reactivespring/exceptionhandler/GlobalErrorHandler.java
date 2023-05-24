package com.reactivespring.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

/**
 * Handy, any error that is encounetred on the Controller, will be caught here first
 */
@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    /**
     * Everytime the WebExchangeBindException is thrown, we
     * will run this method
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleRequestBodyError(WebExchangeBindException e) {
        log.error("Exception Caught in handleRequestBodyError : {} ", e.getMessage(), e);

        // gives you the binding errors and all list of error
        var error = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted()
                .collect(Collectors.joining(","));
        log.error("Error is: {} ", error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
