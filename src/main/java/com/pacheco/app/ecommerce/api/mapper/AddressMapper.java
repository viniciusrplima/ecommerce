package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.AddressInput;
import com.pacheco.app.ecommerce.api.model.output.AddressModel;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.EmbeddableAddress;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressMapper {

    @Autowired
    private ModelMapper modelMapper;

    public AddressModel toRepresentation(Address address) {
        return modelMapper.map(address, AddressModel.class);
    }

    public List<AddressModel> toRepresentationList(List<Address> addressList) {
        return addressList.stream()
                .map(address -> toRepresentation(address))
                .collect(Collectors.toList());
    }

    public Address toModel(AddressInput addressDTO) {
        return modelMapper.map(addressDTO, Address.class);
    }
}
