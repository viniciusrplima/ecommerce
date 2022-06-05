package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.ProductTypeInput;
import com.pacheco.app.ecommerce.api.model.output.ProductTypeModel;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductTypeMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ProductType toModel(ProductTypeInput productTypeInput) {
        return modelMapper.map(productTypeInput, ProductType.class);
    }

    public ProductTypeModel toRepresentation(ProductType productType) {
        return modelMapper.map(productType, ProductTypeModel.class);
    }

    public List<ProductTypeModel> toRepresentationList(List<ProductType> productTypeList) {
        return productTypeList.stream()
                .map(this::toRepresentation)
                .collect(Collectors.toList());
    }

    public void mergeProductType(ProductTypeInput productTypeInput, ProductType productType) {
        modelMapper.map(productTypeInput, productType);
    }

}
