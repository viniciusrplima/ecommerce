package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.BatchNotFoundException;
import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.ProductNotFoundException;
import com.pacheco.app.ecommerce.domain.model.Batch;
import com.pacheco.app.ecommerce.domain.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class BatchService {

    @Autowired private BatchRepository batchRepository;
    @Autowired private ProductService productService;

    public Batch findById(Long batchId) {
        return batchRepository.findById(batchId)
                .orElseThrow(() -> new BatchNotFoundException(batchId));
    }

    @Transactional
    public Batch saveBatch(Batch batch) {
        try {
            batch.setProduct(productService.findById(batch.getProduct().getId()));
        } catch (ProductNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
        return batchRepository.save(batch);
    }

}
