package com.provider.example.rest;

import com.provider.example.dto.request.TransactionPayoutRequestDto;
import com.provider.example.dto.request.TransactionTopUpRequestDto;
import com.provider.example.dto.response.InProgressResponseDto;
import com.provider.example.dto.response.TransactionPayoutResponseDto;
import com.provider.example.dto.response.TransactionTopUpResponseDto;
import com.provider.example.mapper.TransactionMapper;
import com.provider.example.model.TransactionType;
import com.provider.example.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/payments/")
public class PaymentProviderRestControllerV1 {
    private final TransactionMapper mapper;
    private final TransactionService transactionService;

    @PostMapping("topups/")
    public Mono<InProgressResponseDto> topUpTransaction(@RequestBody TransactionTopUpRequestDto transactionTopUpRequestDto, @AuthenticationPrincipal Authentication authentication) {
        return transactionService
                .processTransaction(mapper.mapTransactionRequestTopUpDtoToTransaction(transactionTopUpRequestDto), authentication.getName()).flatMap(transaction ->
                        Mono.just(InProgressResponseDto
                                .builder()
                                .transactionId(transaction.getId())
                                .status(transaction.getStatus())
                                .message(transaction.getMessage()).build()));
    }

    @PostMapping("payout/")
    public Mono<InProgressResponseDto> payoutTransaction(@RequestBody TransactionPayoutRequestDto transactionPayoutRequestDto, @AuthenticationPrincipal Authentication authentication) {
        return transactionService
                .processTransaction(mapper.mapTransactionRequestPayoutDtoToTransaction(transactionPayoutRequestDto), authentication.getName())
                .flatMap(transaction ->
                        Mono.just(InProgressResponseDto
                                .builder()
                                .transactionId(transaction.getId())
                                .status(transaction.getStatus())
                                .message(transaction.getMessage()).build()));
    }

    @GetMapping("transaction/{transactionId}/details")
    public Mono<TransactionTopUpResponseDto> getTransactionById(@PathVariable String transactionId) {
        return transactionService.getTransactionByIdAndType(transactionId, TransactionType.TOP_UP).map(mapper::mapTransactionToTransactionResponseTopUpDto);
    }

    @GetMapping("payout/{payoutId}/details")
    public Mono<TransactionPayoutResponseDto> getTransactionPayoutById(@PathVariable String payoutId) {
        return transactionService.getTransactionByIdAndType(payoutId, TransactionType.PAYOUT).map(mapper::mapTransactionToTransactionResponsePayoutDto);
    }

    @GetMapping("transaction/list")
    public Flux<TransactionTopUpResponseDto> getAllTransaction(@RequestParam(value = "start_date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                               @RequestParam(value = "end_date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return transactionService.getAllTransactionsByDateAndType(startDate, endDate, TransactionType.TOP_UP).map(mapper::mapTransactionToTransactionResponseTopUpDto);
    }


    @GetMapping("payout/list")
    public Flux<TransactionPayoutResponseDto> getAllTransactionPayout(@RequestParam(value = "start_date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                      @RequestParam(value = "end_date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return transactionService.getAllTransactionsByDateAndType(startDate, endDate, TransactionType.PAYOUT).map(mapper::mapTransactionToTransactionResponsePayoutDto);
    }
}
