package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.EntityUsedException;
import com.pacheco.app.ecommerce.domain.exception.ProductTypeNotFoundException;
import com.pacheco.app.ecommerce.domain.model.Image;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStream;

@Service
public class ProductTypeService {

    @Autowired
    private ProductTypeRepository repository;

    @Autowired
    private ImageService imageService;

    public ProductType findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductTypeNotFoundException(id));
    }

    @Transactional
    public ProductType save(ProductType productTypeInput) {
        return repository.save(productTypeInput);
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
    public ProductType updateImage(Long productTypeid, InputStream inputStream, String contentType) {
        ProductType productType = findById(productTypeid);

        if (productType.getImage() != null) {
            imageService.deleteImage(productType.getImage());
        }

        Image image = imageService.saveImage(inputStream, contentType);
        productType.setImage(image);

        return repository.save(productType);
    }
}
