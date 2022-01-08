package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.dto.ProductDTO;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(Routes.PRODUCTS)
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getProducts() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product saveProduct(ProductDTO dto, @RequestParam("photo") Optional<MultipartFile> photo) {
        return productService.saveProduct(dto, photo.orElse(null));
    }
}
