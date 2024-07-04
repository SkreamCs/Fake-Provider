package com.provider.example.exception;

public class InvalidPayoutException extends Exception {
    public InvalidPayoutException(String message) {
        super(message);
    }
}
