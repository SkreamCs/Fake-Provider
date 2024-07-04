package com.provider.example.service;

import com.provider.example.model.Customer;
import com.provider.example.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Mono<Customer> save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Mono<Customer> saveCustomerTransactional(Customer customer) {
        return customerRepository.findById(customer.getCardNumber()).switchIfEmpty(customerRepository.saveNewCustomer(customer));
    }

    public Mono<Customer> getCustomerById(String numberCard) {
        return customerRepository.findById(numberCard);
    }
}
