package com.provider.example.repository;

import com.provider.example.model.Merchant;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends R2dbcRepository<Merchant, String> {
}
