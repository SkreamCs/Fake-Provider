package com.provider.example.repository;

import com.provider.example.model.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends R2dbcRepository<Customer, String> {
    @Query("INSERT INTO customers(card_number, first_name, last_name, country) VALUES (:#{#customer.cardNumber}, :#{#customer.firstName}, :#{#customer.lastName}, :#{#customer.country}) RETURNING *")
    Mono<Customer> saveNewCustomer(Customer customer);
}
