package com.provider.example.security;

import com.provider.example.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MerchantDetailsService implements ReactiveUserDetailsService {
    private final MerchantService merchantService;


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return merchantService.getMerchantByMerchantId(username).map(merchant -> User.withUsername(merchant.getMerchantId()).password(merchant.getSecretKey()).build());
    }
}
