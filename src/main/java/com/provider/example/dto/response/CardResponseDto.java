package com.provider.example.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.ToString;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CardResponseDto(
        String cardNumber
) {
    @ToString.Include(name = "numberCard")
    private String maskNumberCard() {
        return cardNumber.substring(cardNumber.length() - 12) + "***" + cardNumber.substring(cardNumber.length() - 4);
    }
}
