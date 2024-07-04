package com.provider.example.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.provider.example.model.Currency;
import com.provider.example.model.PaymentMethod;
import com.provider.example.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TransactionPayoutRequestDto {
    private Currency currency;
    private BigDecimal amount;
    private String language;
    private String notificationUrl;
    private PaymentMethod paymentMethod;
    private CardRequestDto cardData;
    private CustomerRequestDto customer;
    private final TransactionType transactionType = TransactionType.PAYOUT;
}
