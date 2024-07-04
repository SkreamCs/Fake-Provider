package com.provider.example.repository;

import com.provider.example.model.Transaction;
import com.provider.example.model.TransactionType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, String> {


    @Query("INSERT INTO transactions(id, card_number, currency, language, transaction_type, amount, created_at, updated_at, notification_url, payment_method, status, message) " +
            "VALUES (:#{#transaction.id}, :#{#transaction.cardNumber}, :#{#transaction.currency}, :#{#transaction.language}, :#{#transaction.transactionType}, :#{#transaction.amount}, :#{#transaction.createdAt}, :#{#transaction.updatedAt},:#{#transaction.notificationUrl}, :#{#transaction.paymentMethod}, :#{#transaction.status}, :#{#transaction.message}) RETURNING *")
    Mono<Transaction> saveNewTransaction(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE DATE (created_at) = :date AND transaction_type = :transactionType")
    Flux<Transaction> findAllTransactionsByDateAndType(LocalDate date, TransactionType transactionType);

    @Query("SELECT * FROM transactions WHERE DATE (created_at) >= :startDate AND DATE (created_at) <= :endDate AND transaction_type = :transactionType ")
    Flux<Transaction> findAllTransactionsByDateAndType(LocalDate startDate, LocalDate endDate, TransactionType transactionType);

    @Query("SELECT * FROM transactions WHERE id = :transactionId AND transaction_type = :transactionType ")
    Mono<Transaction> findByIdAndTransactionType(String transactionId, TransactionType transactionType);
}
