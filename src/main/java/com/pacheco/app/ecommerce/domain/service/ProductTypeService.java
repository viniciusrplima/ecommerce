package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.EntityUsedException;
import com.pacheco.app.ecommerce.domain.exception.ProductTypeNotFoundException;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ProductTypeService {

    @Autowired
    private ProductTypeRepository repository;

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

}
