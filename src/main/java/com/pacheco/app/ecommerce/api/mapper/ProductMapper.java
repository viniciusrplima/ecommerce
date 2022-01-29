package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.ProductInput;
import com.pacheco.app.ecommerce.api.model.output.ProductModel;
import com.pacheco.app.ecommerce.domain.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Product toModel(ProductInput productInput) {
        return modelMapper.map(productInput, Product.class);
    }

    public void mergeProduct(ProductInput productInput, Product product) {
        modelMapper.map(productInput, product);
    }

    public ProductModel toRepresentation(Product product) {
        return modelMapper.map(product, ProductModel.class);
    }

    public List<ProductModel> toRepresentationList(List<Product> productList) {
        return productList.stream()
                .map(this::toRepresentation)
                .collect(Collectors.toList());
    }

}
