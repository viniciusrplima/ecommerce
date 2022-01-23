package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.dto.ProductDTO;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(Routes.PRODUCTS)
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getProducts(@RequestParam(value = "q", required = false) String query,
                                     @RequestParam(value = "limit", required = false) Long limit,
                                     @RequestParam(value = "page", required = false) Long page,
                                     @RequestParam(value = "type", required = false) Long type) {
        return repository.findWithAttrbutes(query, type, limit, page);
    }

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable Long productId) {
        return productService.findById(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product saveProduct(@Valid ProductDTO dto, @RequestParam("photo") Optional<MultipartFile> photo) {
        return productService.saveProduct(dto, photo.orElse(null));
    }

    @PutMapping("/{productId}")
    public Product updateProduct(
            @PathVariable Long productId, @Valid ProductDTO dto,
            @RequestParam("photo") Optional<MultipartFile> photo) {

        return productService.update(productId, dto, photo.orElse(null));
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long productId) {
        productService.delete(productId);
    }
}
