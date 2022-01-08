package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Routes.PRODUCT_TYPES)
public class ProductTypeController {

    @Autowired
    private ProductTypeRepository repository;

    @GetMapping
    public List<ProductType> getProductTypes() {
        return repository.findAll();
    }

}
