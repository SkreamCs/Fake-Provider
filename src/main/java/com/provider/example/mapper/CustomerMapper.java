package com.provider.example.mapper;

import com.provider.example.dto.request.CustomerRequestDto;
import com.provider.example.dto.response.CustomerResponseDto;
import com.provider.example.model.Customer;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface CustomerMapper {
    CustomerResponseDto mapDto(Customer customer);

    @InheritInverseConfiguration
    Customer mapFromDto(CustomerRequestDto customerRequestDto);
}
