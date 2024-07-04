package com.provider.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("transactions")
public class Transaction {
    @Id
    private String id;
    private String cardNumber;
    private Currency currency;
    private String language;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String notificationUrl;
    private PaymentMethod paymentMethod;
    private Status status;
    private String message;
    @Transient
    private Card cardData;
    @Transient
    private Customer customer;
}
