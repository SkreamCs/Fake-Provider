package com.provider.example.mapper;

import com.provider.example.dto.request.CardRequestDto;
import com.provider.example.dto.response.CardResponseDto;
import com.provider.example.model.Card;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface CardMapper {
    CardResponseDto mapDto(Card card);

    @InheritInverseConfiguration
    Card mapFromDto(CardRequestDto requestDto);

}