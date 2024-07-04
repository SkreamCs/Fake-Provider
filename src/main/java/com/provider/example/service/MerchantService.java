package com.provider.example.service;

import com.provider.example.model.Merchant;
import com.provider.example.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public Mono<Merchant> getMerchantByMerchantId(String merchantId) {
        return merchantRepository.findById(merchantId);
    }

    public Mono<Merchant> update(Merchant merchant) {
        return merchantRepository.save(merchant);
    }
}
