package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
