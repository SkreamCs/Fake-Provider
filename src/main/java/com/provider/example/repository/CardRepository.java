package com.provider.example.repository;

import com.provider.example.model.Card;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends R2dbcRepository<Card, String> {
}

