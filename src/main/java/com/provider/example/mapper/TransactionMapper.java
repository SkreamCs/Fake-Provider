package com.provider.example.mapper;

import com.provider.example.dto.request.TransactionPayoutRequestDto;
import com.provider.example.dto.request.TransactionTopUpRequestDto;
import com.provider.example.dto.response.TransactionPayoutResponseDto;
import com.provider.example.dto.response.TransactionTopUpResponseDto;
import com.provider.example.model.Transaction;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {CustomerMapper.class, CardMapper.class})
public interface TransactionMapper {


    Transaction mapTransactionRequestTopUpDtoToTransaction(TransactionTopUpRequestDto transactionTopUpRequestDto);


    TransactionTopUpResponseDto mapTransactionToTransactionResponseTopUpDto(Transaction transaction);

    Transaction mapTransactionRequestPayoutDtoToTransaction(TransactionPayoutRequestDto transactionPayoutRequestDto);

    TransactionPayoutResponseDto mapTransactionToTransactionResponsePayoutDto(Transaction transaction);


}

