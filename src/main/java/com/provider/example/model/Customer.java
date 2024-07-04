package com.provider.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("customers")
public class Customer {
    @Id
    private String cardNumber;
    private String firstName;
    private String lastName;
    private Country country;
    @CreatedDate
    private LocalDateTime createdAt;
    private Status status;
}
