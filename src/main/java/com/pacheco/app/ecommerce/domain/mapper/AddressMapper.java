package com.pacheco.app.ecommerce.domain.mapper;

import com.pacheco.app.ecommerce.api.dto.AddressDTO;
import com.pacheco.app.ecommerce.domain.model.Address;

public class AddressMapper {

    public static Address toAddress(AddressDTO addressDTO) {
        return Address.builder()
                .state(addressDTO.getState())
                .city(addressDTO.getCity())
                .cep(addressDTO.getCep())
                .district(addressDTO.getDistrict())
                .street(addressDTO.getStreet())
                .number(addressDTO.getNumber())
                .complement(addressDTO.getComplement())
                .referencePoint(addressDTO.getReferencePoint())
                .build();
    }
}
