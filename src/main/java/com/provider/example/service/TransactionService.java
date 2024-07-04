package com.provider.example.service;

import com.provider.example.exception.InvalidPaymentMethodException;
import com.provider.example.exception.InvalidPayoutException;
import com.provider.example.model.*;
import com.provider.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.provider.example.model.TransactionType.TOP_UP;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CardService cardService;
    private final MerchantService merchantService;
    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;
    private final WebhookService webhookService;


    protected Mono<Transaction> validateTransaction(Transaction transaction, Merchant merchant) {
        Transaction validateTransaction = transaction.toBuilder()
                .id(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .status(Status.IN_PROGRESS)
                .message("OK")
                .build();

        switch (validateTransaction.getTransactionType()) {
            case TOP_UP -> {
                if (validateTransaction.getPaymentMethod() != PaymentMethod.CARD) {
                    return webhookService.sendWebhook(transaction.toBuilder().status(Status.FAILED).message("PAYMENT_METHOD_NOT_ALLOWED").build()).then(Mono.error(new InvalidPaymentMethodException("PAYMENT_METHOD_NOT_ALLOWED")));
                }
                return webhookService.sendWebhook(validateTransaction);

            }
            case PAYOUT -> {
                if (validateTransaction.getAmount().compareTo(merchant.getBalance()) > 0) {
                    return webhookService.sendWebhook(validateTransaction.toBuilder().status(Status.FAILED).message("PAYOUT_MIN_AMOUNT").build()).then(Mono.error(new InvalidPayoutException("PAYOUT_MIN_AMOUNT")));
                }
                return webhookService.sendWebhook(validateTransaction);
            }
        }
        return Mono.error(new Exception("Invalid transaction type"));
    }

    @Transactional
    public Mono<Transaction> processTransaction(Transaction transaction, String merchantId) {
        return merchantService.getMerchantByMerchantId(merchantId).flatMap(merchant -> validateTransaction(transaction, merchant)
                .flatMap(transactionValidate -> cardService.getCardByNumberCard(transactionValidate.getCardData().getCardNumber())
                        .flatMap(card -> {
                            if (Objects.requireNonNull(transactionValidate.getTransactionType()) == TOP_UP) {
                                card.setBalance(card.getBalance().subtract(transactionValidate.getAmount()));
                                merchant.setBalance(merchant.getBalance().add(transactionValidate.getAmount()));
                            } else {
                                merchant.setBalance(merchant.getBalance().subtract(transactionValidate.getAmount()));
                                card.setBalance(card.getBalance().add(transactionValidate.getAmount()));
                            }
                            return merchantService.update(merchant).then(cardService.update(card))
                                    .flatMap(updateCard -> customerService.saveCustomerTransactional(transactionValidate.getCustomer().toBuilder().cardNumber(updateCard.getCardNumber()).build()))
                                    .then(save(transactionValidate.toBuilder().cardNumber(card.getCardNumber()).build()).flatMap(webhookService::sendWebhook));

                        })));
    }


    public Mono<Transaction> save(Transaction transaction) {
        return transactionRepository.saveNewTransaction(transaction.toBuilder().status(Status.SUCCESS).build());
    }

    private Flux<Transaction> getAllTransactionsByDateAndType(LocalDate localDate, TransactionType transactionType) {
        return transactionRepository.findAllTransactionsByDateAndType(localDate, transactionType);
    }

    public Flux<Transaction> getAllTransactionsByDateAndType(LocalDate startDate, LocalDate endDate, TransactionType transactionType) {
        if (startDate == null || endDate == null) {
            return getAllTransactionsByDateAndType(LocalDate.now(), transactionType);
        } else {
            return transactionRepository.findAllTransactionsByDateAndType(startDate, endDate, transactionType);
        }
    }

    public Mono<Transaction> getTransactionByIdAndType(String transactionId, TransactionType transactionType) {
        return transactionRepository.findByIdAndTransactionType(transactionId, transactionType)
                .flatMap(transaction ->
                        customerService.getCustomerById(transaction.getCardNumber())
                                .map(customer ->
                                        transaction.toBuilder().customer(customer).build()));
    }
}
