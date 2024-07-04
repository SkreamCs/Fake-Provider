package com.provider.example.service;

import com.provider.example.model.*;
import com.provider.example.repository.TransactionRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static com.provider.example.model.Status.IN_PROGRESS;
import static com.provider.example.model.Status.SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CardService cardService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private CustomerService customerService;
    @Mock
    private WebhookService webhookService;
    @InjectMocks
    private TransactionService transactionService;

    static Stream<Transaction> transactionDataTestStream() {
        return Stream.of(
                Transaction.builder()
                        .amount(BigDecimal.valueOf(1000))
                        .paymentMethod(PaymentMethod.CARD)
                        .cardData(Card.builder()
                                .cardNumber("11111111111111111")
                                .cvv("545").expDate("10/11")
                        .build()).language("en")
                        .currency(Currency.BRL)
                        .customer(Customer.builder().lastName("Doe").firstName("John").country(Country.BR).build())
                        .notificationUrl("https://webhook.site/2e80cfc7-ddfd-4ac1-9ae7-7e059dcadcbc")
                        .transactionType(TransactionType.TOP_UP)
                        .build(),
                Transaction.builder()
                        .amount(BigDecimal.valueOf(1000))
                        .paymentMethod(PaymentMethod.CARD)
                        .cardData(Card.builder()
                                .cardNumber("11111111111111111")
                                .cvv("545").expDate("10/11")
                                .build()).language("en")
                        .currency(Currency.BRL)
                        .customer(Customer.builder().lastName("Doe").firstName("John").country(Country.BR).build())
                        .notificationUrl("https://webhook.site/2e80cfc7-ddfd-4ac1-9ae7-7e059dcadcbc")
                        .transactionType(TransactionType.PAYOUT)
                        .build()
        );
    }
    @ParameterizedTest
    @MethodSource("transactionDataTestStream")
    void validateTransactionTest(Transaction transaction) {
        String merchantId = UUID.randomUUID().toString();
        Merchant merchant = Merchant.builder().merchantId(merchantId).secretKey("123abc").balance(BigDecimal.valueOf(10000)).build();
        Transaction validatedTransaction = transaction.toBuilder().message("OK").status(IN_PROGRESS).id(UUID.randomUUID().toString()).build();
        when(webhookService.sendWebhook(any(Transaction.class))).thenReturn(Mono.just(validatedTransaction));

        StepVerifier.create(transactionService.validateTransaction(transaction, merchant)).expectNext(validatedTransaction).verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("transactionDataTestStream")
    void saveTransactionTest(Transaction transaction) {
        Transaction savedTransaction = transaction.toBuilder().message("OK").status(SUCCESS).id(UUID.randomUUID().toString()).build();
        when(transactionRepository.saveNewTransaction(any(Transaction.class))).thenReturn(Mono.just(savedTransaction));

        StepVerifier.create(transactionService.save(transaction)).expectNext(savedTransaction).verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("transactionDataTestStream")
    void processTransactionTest(Transaction transaction) {

        String merchantId = UUID.randomUUID().toString();
        Merchant merchant = Merchant.builder().merchantId(merchantId).secretKey("123abc").balance(BigDecimal.valueOf(10000)).build();

        when(merchantService.getMerchantByMerchantId(any(String.class))).thenReturn(Mono.just(merchant));

        Transaction validatedTransaction = transaction.toBuilder().id(UUID.randomUUID().toString()).status(IN_PROGRESS).message("OK").createdAt(LocalDateTime.now()).build();

        Card cardByNumberCard = Card.builder().cardNumber("1111111111111111").cvv("545").expDate("10/11").balance(BigDecimal.valueOf(1000000)).createdAt(LocalDateTime.now()).build();
        when(cardService.getCardByNumberCard(any(String.class))).thenReturn(Mono.just(cardByNumberCard));

        when(merchantService.update(any(Merchant.class))).thenReturn(Mono.just(merchant));

        when(cardService.update(any(Card.class))).thenReturn(Mono.just(cardByNumberCard));

        Customer savedCustomer = validatedTransaction.getCustomer().toBuilder().cardNumber(validatedTransaction.getCardNumber()).build();
        when(customerService.saveCustomerTransactional(any(Customer.class))).thenReturn(Mono.just(savedCustomer));

        Transaction savedTransaction = validatedTransaction.toBuilder().status(SUCCESS).message("OK").build();
        when(transactionRepository.saveNewTransaction(any())).thenReturn(Mono.just(savedTransaction));
        when(webhookService.sendWebhook(any(Transaction.class))).thenReturn(Mono.just(savedTransaction));

        Mono<Transaction> actualTransaction = transactionService.processTransaction(transaction, merchantId);

        StepVerifier.create(actualTransaction).expectNext(savedTransaction).verifyComplete();

    }
    @ParameterizedTest
    @MethodSource("transactionDataTestStream")
    void getAllTransactionByDayTest(Transaction transaction) {
        Transaction savedTransaction = transaction.toBuilder().cardNumber(transaction.getCardNumber()).id(UUID.randomUUID().toString()).status(SUCCESS).message("OK").createdAt(LocalDateTime.now()).build();
        when(transactionRepository.findAllTransactionsByDateAndType(any(LocalDate.class), any(TransactionType.class))).thenReturn(Flux.just(savedTransaction));

        StepVerifier.create(transactionService.getAllTransactionsByDateAndType(null, null, transaction.getTransactionType())).expectNext(savedTransaction).verifyComplete();
    }
    @ParameterizedTest
    @MethodSource("transactionDataTestStream")
    void getAllTransactionByDateTest(Transaction transaction) {
        Transaction savedTransaction = transaction.toBuilder().cardNumber(transaction.getCardNumber()).id(UUID.randomUUID().toString()).status(SUCCESS).message("OK").createdAt(LocalDateTime.now()).build();
        when(transactionRepository.findAllTransactionsByDateAndType(any(LocalDate.class), any(LocalDate.class), any(TransactionType.class))).thenReturn(Flux.just(savedTransaction));

        StepVerifier.create(transactionService.getAllTransactionsByDateAndType(savedTransaction.getCreatedAt().toLocalDate(), savedTransaction.getCreatedAt().toLocalDate(), transaction.getTransactionType()))
                .expectNext(savedTransaction).verifyComplete();
    }
    @ParameterizedTest
    @MethodSource("transactionDataTestStream")
    void getTransactionByIdAndTypeTest(Transaction transaction) {
        Customer savedCustomer = transaction.getCustomer().toBuilder().cardNumber(transaction.getCardData().getCardNumber()).build();
        Transaction savedTransaction = transaction.toBuilder().customer(savedCustomer).cardNumber(transaction.getCardData().getCardNumber()).id(UUID.randomUUID().toString()).status(SUCCESS).message("OK").createdAt(LocalDateTime.now()).build();
        when(transactionRepository.findByIdAndTransactionType(any(String.class), any(TransactionType.class))).thenReturn(Mono.just(savedTransaction));
        when(customerService.getCustomerById(any(String.class))).thenReturn(Mono.just(savedTransaction.getCustomer()));
        StepVerifier.create(transactionService.getTransactionByIdAndType(savedTransaction.getId(), transaction.getTransactionType()))
                .expectNext(savedTransaction).verifyComplete();
    }
}
