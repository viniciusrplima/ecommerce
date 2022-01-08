package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.api.dto.ProductDTO;
import com.pacheco.app.ecommerce.domain.exception.CouldNotOpenImageException;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.pacheco.app.ecommerce.domain.mapper.ProductMapper.toProduct;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Product saveProduct(ProductDTO dto, MultipartFile photo) {
        Product product = null;

        try {
            product = toProduct(dto, photo);
        } catch (IOException e) {
            throw new CouldNotOpenImageException(e);
        }

        return repository.save(product);
    }

}
