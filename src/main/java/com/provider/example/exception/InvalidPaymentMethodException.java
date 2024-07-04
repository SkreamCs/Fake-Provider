package com.provider.example.exception;

public class InvalidPaymentMethodException extends Exception {
    public InvalidPaymentMethodException(String message) {
        super(message);
    }
}
