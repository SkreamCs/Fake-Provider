package com.provider.example.integration.controller;

import com.provider.example.dto.request.CardRequestDto;
import com.provider.example.dto.request.CustomerRequestDto;
import com.provider.example.dto.request.TransactionPayoutRequestDto;
import com.provider.example.dto.request.TransactionTopUpRequestDto;
import com.provider.example.dto.response.InProgressResponseDto;
import com.provider.example.dto.response.TransactionPayoutResponseDto;
import com.provider.example.dto.response.TransactionTopUpResponseDto;
import com.provider.example.model.Country;
import com.provider.example.model.Currency;
import com.provider.example.model.PaymentMethod;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Objects;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test-containers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentProviderRestControllerV1Test {
    @Autowired
    WebTestClient webTestClient;
    @LocalServerPort
   private Integer port;
    private String baseUrl;
    private String basicAuth;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withInitScript("data.sql");

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d/api/v1/payments/", port );
        basicAuth = Base64.getEncoder().encodeToString("1234:1234".getBytes(StandardCharsets.UTF_8));
    }
    @Order(1)
    @Test
    void topUpTransactionTest() {
        TransactionTopUpRequestDto transactionTopUpRequestDto = TransactionTopUpRequestDto.builder()
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000))
                .cardData(new CardRequestDto("1111111111111111", "545", "10/11"))
                .language("en")
                .currency(Currency.BRL)
                .notificationUrl("https://webhook.site/2e80cfc7-ddfd-4ac1-9ae7-7e059dcadcbc")
                .customer(new CustomerRequestDto("John", "Doe", Country.BR))
                .build();
        webTestClient.post().uri(baseUrl + "topups/")
                .header("Authorization", "Basic " + basicAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionTopUpRequestDto)
                .exchange()
                .expectStatus().isOk();
    }
    @Order(2)
    @Test
    void payoutTransactionTest() {
        TransactionPayoutRequestDto transactionPayoutRequestDto = TransactionPayoutRequestDto.builder()
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000))
                .cardData(CardRequestDto.builder().cardNumber("1111111111111111").build())
                .language("en")
                .currency(Currency.BRL)
                .notificationUrl("https://webhook.site/2e80cfc7-ddfd-4ac1-9ae7-7e059dcadcbc")
                .customer(new CustomerRequestDto("John", "Doe", Country.BR))
                .build();
        webTestClient.post().uri(baseUrl + "payout/")
                .header("Authorization", "Basic " + basicAuth)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionPayoutRequestDto)
                .exchange()
                .expectStatus().isOk();
    }
    @Test
    @Order(3)
    void getAllTransaction() {
        webTestClient.get().uri(baseUrl + "transaction/list")
                .header("Authorization", "Basic " + basicAuth)
                .exchange().expectBody().jsonPath("$.size()").isEqualTo(1);


        String uri = UriComponentsBuilder.fromUriString(baseUrl + "transaction/list")
                .queryParam("startDate", LocalDate.now())
                .queryParam("endDate", LocalDate.now())
                .toUriString();
        webTestClient.get().uri(uri)
                .header("Authorization", "Basic " + basicAuth)
                .exchange().expectBodyList(TransactionTopUpResponseDto.class).hasSize(1);
        }
    @Test
    @Order(4)
    void getAllTransactionPayout() {
        webTestClient.get().uri(baseUrl + "payout/list")
                .header("Authorization", "Basic " + basicAuth)
                .exchange().expectBody().jsonPath("$.size()").isEqualTo(1);

        String uri = UriComponentsBuilder.fromUriString(baseUrl + "payout/list")
                .queryParam("start_date", LocalDate.now())
                .queryParam("end_date", LocalDate.now())
                .toUriString();
        webTestClient.get().uri(uri)
                .header("Authorization", "Basic " + basicAuth)
                .exchange().expectBodyList(TransactionPayoutResponseDto.class).hasSize(1);


    }
    @Order(5)
    @Test
    void getTransactionById() {
        TransactionTopUpRequestDto transactionTopUpRequestDto = TransactionTopUpRequestDto.builder()
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000))
                .cardData(new CardRequestDto("1111111111111111", "545", "10/11"))
                .language("en")
                .currency(Currency.BRL)
                .notificationUrl("https://webhook.site/2e80cfc7-ddfd-4ac1-9ae7-7e059dcadcbc")
                .customer(new CustomerRequestDto("John", "Doe", Country.BR))
                .build();
        String transactionId = Objects.requireNonNull(webTestClient.post().uri(baseUrl + "topups/")
                        .header("Authorization", "Basic " + basicAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionTopUpRequestDto)
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult(InProgressResponseDto.class)
                        .getResponseBody()
                        .blockFirst())
                .transactionId();

        webTestClient.get().uri(baseUrl + "transaction/{transactionId}/details", transactionId)
                .header("Authorization", "Basic " + basicAuth)
                .exchange().expectBody(TransactionTopUpResponseDto.class);


    }
    @Order(6)
    @Test
    void getTransactionPayoutById() {
        TransactionPayoutRequestDto transactionPayoutRequestDto = TransactionPayoutRequestDto.builder()
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000))
                .cardData(CardRequestDto.builder().cardNumber("1111111111111111").build())
                .language("en")
                .currency(Currency.BRL)
                .notificationUrl("https://webhook.site/2e80cfc7-ddfd-4ac1-9ae7-7e059dcadcbc")
                .customer(new CustomerRequestDto("John", "Doe", Country.BR))
                .build();

        String transactionId = Objects.requireNonNull(webTestClient.post().uri(baseUrl + "payout/")
                        .header("Authorization", "Basic " + basicAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionPayoutRequestDto)
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult(InProgressResponseDto.class)
                        .getResponseBody()
                        .blockFirst())
                .transactionId();

        webTestClient.get().uri(baseUrl + "payout/{payoutId}/details", transactionId)
                .header("Authorization", "Basic " + basicAuth)
                .exchange().expectBody(TransactionPayoutResponseDto.class);



    }
}
