package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.api.model.input.ProductTypeInput;
import com.pacheco.app.ecommerce.domain.exception.EntityUsedException;
import com.pacheco.app.ecommerce.domain.exception.ProductTypeNotFoundException;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import static com.pacheco.app.ecommerce.api.mapper.ProductTypeMapper.mergeProductType;
import static com.pacheco.app.ecommerce.api.mapper.ProductTypeMapper.toProductType;

@Service
public class ProductTypeService {

    @Autowired
    private ProductTypeRepository repository;

    public ProductType findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductTypeNotFoundException(id));
    }

    @Transactional
    public ProductType save(ProductTypeInput dto, MultipartFile icon) {
        return repository.save(toProductType(dto, icon));
    }

    @Transactional
    public ProductType update(Long id, ProductTypeInput productTypeDTO, MultipartFile icon) {
        ProductType productType = findById(id);
        productType = mergeProductType(productType, productTypeDTO, icon);

        return repository.save(productType);
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
