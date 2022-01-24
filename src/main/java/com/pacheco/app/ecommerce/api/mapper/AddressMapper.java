package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.AddressInput;
import com.pacheco.app.ecommerce.domain.model.Address;

public class AddressMapper {

    public static Address toAddress(AddressInput addressDTO) {
        return Address.entityBuilder()
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
