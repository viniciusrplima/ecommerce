package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return repository.findAll();
    }


}
