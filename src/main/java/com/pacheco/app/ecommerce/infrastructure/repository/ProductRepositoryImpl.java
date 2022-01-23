package com.pacheco.app.ecommerce.infrastructure.repository;

import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductRepositoryQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.pacheco.app.ecommerce.infrastructure.repository.spec.ProductSpecs.withLikeName;
import static com.pacheco.app.ecommerce.infrastructure.repository.spec.ProductSpecs.withType;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryQueries {

    @PersistenceContext
    private EntityManager manager;

    @Autowired @Lazy
    private ProductRepository productRepository;

    @Override
    @Transactional
    public List<Product> findWithAttrbutes(String query, Long type, Long limit, Long page) {
        List<Specification<Product>> specs = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            specs.add(withLikeName(query));
        }

        if (type != null) {
            specs.add(withType(type));
        }

        return productRepository.findAllPaginated(specs, limit, page);
    }

}
