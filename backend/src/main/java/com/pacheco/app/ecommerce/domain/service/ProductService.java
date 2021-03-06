package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.EntityUsedException;
import com.pacheco.app.ecommerce.domain.exception.OutOfStockException;
import com.pacheco.app.ecommerce.domain.exception.ProductNotEnoughException;
import com.pacheco.app.ecommerce.domain.exception.ProductNotFoundException;
import com.pacheco.app.ecommerce.domain.model.Image;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.math.BigInteger;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductTypeService productTypeService;

    @Autowired
    private ImageService imageService;

    @Transactional
    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new EntityUsedException(id);
        }
    }

    @Transactional
    public Product updateImage(Long productid, InputStream inputStream, String contentType) {
        Product product = findById(productid);

        if (product.getImage() != null) {
            imageService.deleteImage(product.getImage());
        }

        Image image = imageService.saveImage(inputStream, contentType);
        product.setImage(image);

        return repository.save(product);
    }
}
