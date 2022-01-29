package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.mapper.ProductMapper;
import com.pacheco.app.ecommerce.api.model.input.ProductInput;
import com.pacheco.app.ecommerce.api.model.output.ProductModel;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(Routes.PRODUCTS)
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public List<ProductModel> getProducts(@RequestParam(value = "q", required = false) String query,
                                          @RequestParam(value = "limit", required = false) Long limit,
                                          @RequestParam(value = "page", required = false) Long page,
                                          @RequestParam(value = "type", required = false) Long type) {
        return productMapper.toRepresentationList(
                repository.findWithAttrbutes(query, type, limit, page));
    }

    @GetMapping("/{productId}")
    public ProductModel getProduct(@PathVariable Long productId) {
        return productMapper.toRepresentation(productService.findById(productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductModel saveProduct(@RequestBody @Valid ProductInput productInput) {
        return productMapper.toRepresentation(
                productService.saveProduct(productMapper.toModel(productInput)));
    }

    @PutMapping("/{productId}")
    public ProductModel updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductInput productInput) {
        Product product = productService.findById(productId);
        productMapper.mergeProduct(productInput, product);
        return productMapper.toRepresentation(productService.saveProduct(product));
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long productId) {
        productService.delete(productId);
    }
}
