package com.provider.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("merchants")
public class Merchant {
    @Id
    private String merchantId;
    private String secretKey;
    private BigDecimal balance;
    @CreatedDate
    private LocalDateTime createdAt;
    private Status status;
}
