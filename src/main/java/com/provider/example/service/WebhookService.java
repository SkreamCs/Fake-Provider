package com.provider.example.service;

import com.provider.example.mapper.TransactionMapper;
import com.provider.example.model.Status;
import com.provider.example.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebhookService {
    private final WebClient webClient = WebClient.builder().build();
    private final TransactionMapper transactionMapper;

    public Mono<Transaction> sendWebhook(Transaction transaction) {
        if (transaction.getStatus() == Status.SUCCESS) {
            return webClient
                    .post()
                    .uri(transaction.getNotificationUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(transactionMapper.mapTransactionToTransactionResponseTopUpDto(transaction).toString())
                    .exchange().thenReturn(transaction);
        } else {
            Map<String, String> body = new LinkedHashMap<>();
            body.put("transaction_id", transaction.getId());
            body.put("status", transaction.getStatus().name());
            body.put("message", transaction.getMessage());
            return webClient
                    .post()
                    .uri(transaction.getNotificationUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .exchange().thenReturn(transaction);
        }

    }
}
