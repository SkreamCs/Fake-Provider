package com.provider.example.service;

import com.provider.example.model.Card;
import com.provider.example.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CardService {
    private final CardRepository cardRepository;

    public Mono<Card> getCardByNumberCard(String numberCard) {
        return cardRepository.findById(numberCard);
    }

    public Mono<Card> update(Card card) {
        return cardRepository.save(card);
    }
}
