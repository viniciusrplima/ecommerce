package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.output.PurchaseModel;
import com.pacheco.app.ecommerce.domain.model.Purchase;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseMapper {

    @Autowired private ModelMapper modelMapper;

    public PurchaseModel toRepresentation(Purchase purchase) {
        return modelMapper.map(purchase, PurchaseModel.class);
    }

    public List<PurchaseModel> toRepresentationList(List<Purchase> purchases) {
        return purchases.stream()
                .map(this::toRepresentation)
                .collect(Collectors.toList());
    }
}
