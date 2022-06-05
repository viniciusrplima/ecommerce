package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.Product;

import java.util.List;

public interface ProductRepositoryQueries {

    public List<Product> findWithAttributes(String query, String type, Long limit, Long page);
    public Long countWithAttributes(String query, String type);

}
