package com.provider.example.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.provider.example.model.Currency;
import com.provider.example.model.PaymentMethod;
import com.provider.example.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionPayoutResponseDto(
        String id,
        Currency currency,
        String language,
        BigDecimal amount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String notificationUrl,
        PaymentMethod paymentMethod,
        Status status,
        String message,
        CardResponseDto cardData,
        CustomerResponseDto customer
) {
}
