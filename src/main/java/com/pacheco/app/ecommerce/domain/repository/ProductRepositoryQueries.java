package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.Product;

import java.util.List;

public interface ProductRepositoryQueries {

    public List<Product> findWithAttrbutes(String query, Long type, Long limit, Long page);

}
