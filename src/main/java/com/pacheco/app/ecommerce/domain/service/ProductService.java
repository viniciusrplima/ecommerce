package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.api.model.input.ProductInput;
import com.pacheco.app.ecommerce.domain.exception.CouldNotOpenImageException;
import com.pacheco.app.ecommerce.domain.exception.EntityUsedException;
import com.pacheco.app.ecommerce.domain.exception.OutOfStockException;
import com.pacheco.app.ecommerce.domain.exception.ProductNotFoundException;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static com.pacheco.app.ecommerce.api.mapper.ProductMapper.mergeProduct;
import static com.pacheco.app.ecommerce.api.mapper.ProductMapper.toProduct;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductTypeService productTypeService;

    @Transactional
    public Product saveProduct(ProductInput dto, MultipartFile photo) {
        Product product = null;

        try {
            product = toProduct(dto, photo);
            product.setTypes(getTypesFromIds(dto.getTypes()));
        } catch (IOException e) {
            throw new CouldNotOpenImageException(e);
        }

        return repository.save(product);
    }

    private List<ProductType> getTypesFromIds(List<Long> productTypeIds) {
        return productTypeIds.stream()
                .map(id -> productTypeService.findById(id))
                .collect(Collectors.toList());
    }

    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public Product update(Long id, ProductInput dto, MultipartFile photo) {
        Product product = findById(id);

        try {
            product = mergeProduct(product, dto, photo);
        } catch (IOException e) {
            throw new CouldNotOpenImageException(e);
        }

        return product;
    }

    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new EntityUsedException(id);
        }
    }

    @Transactional
    public Product getProductFromStock(Long productId) {
        Product product = findById(productId);

        if (product.getStock().equals(BigInteger.ZERO)) {
            throw new OutOfStockException(product.getName());
        }

        product.setStock(product.getStock().subtract(BigInteger.ONE));
        return repository.save(product);
    }
}
