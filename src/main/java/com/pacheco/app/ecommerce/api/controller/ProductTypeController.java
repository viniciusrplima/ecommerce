package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.mapper.ProductTypeMapper;
import com.pacheco.app.ecommerce.api.model.input.ProductTypeInput;
import com.pacheco.app.ecommerce.api.model.output.ProductTypeModel;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import com.pacheco.app.ecommerce.domain.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(Routes.PRODUCT_TYPES)
@CrossOrigin
public class ProductTypeController {

    @Autowired
    private ProductTypeRepository repository;

    @Autowired
    private ProductTypeService service;

    @Autowired
    private ProductTypeMapper productTypeMapper;

    @GetMapping
    public List<ProductTypeModel> getProductTypes() {
        return productTypeMapper.toRepresentationList(repository.findAll());
    }

    @GetMapping("/{productTypeId}")
    public ProductTypeModel getProductType(@PathVariable Long productTypeId) {
        return productTypeMapper.toRepresentation(service.findById(productTypeId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductTypeModel saveProductType(@RequestBody @Valid ProductTypeInput productTypeInput) {
        return productTypeMapper.toRepresentation(
                service.save(productTypeMapper.toModel(productTypeInput)));
    }

    @PutMapping("/{productTypeId}")
    public ProductTypeModel updateProductType(@PathVariable Long productTypeId,
                                              @RequestBody @Valid ProductTypeInput productTypeInput) {
        ProductType productType = service.findById(productTypeId);
        productTypeMapper.mergeProductType(productTypeInput, productType);
        return productTypeMapper.toRepresentation(service.save(productType));
    }

    @DeleteMapping("/{productTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductType(@PathVariable Long productTypeId) {
        service.delete(productTypeId);
    }

}
