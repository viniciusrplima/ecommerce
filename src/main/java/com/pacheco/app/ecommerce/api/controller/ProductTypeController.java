package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.model.input.ProductTypeInput;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import com.pacheco.app.ecommerce.domain.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Routes.PRODUCT_TYPES)
@CrossOrigin
public class ProductTypeController {

    @Autowired
    private ProductTypeRepository repository;

    @Autowired
    private ProductTypeService service;

    @GetMapping
    public List<ProductType> getProductTypes() {
        return repository.findAll();
    }

    @GetMapping("/{productTypeId}")
    public ProductType getProductType(@PathVariable Long productTypeId) {
        return service.findById(productTypeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductType saveProductType(
            @Valid ProductTypeInput productType, @RequestParam("icon") Optional<MultipartFile> icon) {
        return service.save(productType, icon.orElse(null));
    }

    @PutMapping("/{productTypeId}")
    public ProductType updateProductType(
            @PathVariable Long productTypeId, @Valid ProductTypeInput productType,
            @RequestParam("icon") Optional<MultipartFile> icon) {
        return service.update(productTypeId, productType, icon.orElse(null));
    }

    @DeleteMapping("/{productTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductType(@PathVariable Long productTypeId) {
        service.delete(productTypeId);
    }

}
