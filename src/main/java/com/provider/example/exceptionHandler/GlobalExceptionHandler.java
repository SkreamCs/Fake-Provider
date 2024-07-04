package com.provider.example.exceptionHandler;

import com.provider.example.exception.InvalidPaymentMethodException;
import com.provider.example.exception.InvalidPayoutException;
import com.provider.example.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidPaymentMethodException.class)
    public ResponseEntity<Map<String, String>> handleTopUpException(InvalidPaymentMethodException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", Status.FAILED.toString());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidPayoutException.class)
    public ResponseEntity<Map<String, String>> handlePayoutException(InvalidPayoutException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("error_code", Status.FAILED.toString());
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
