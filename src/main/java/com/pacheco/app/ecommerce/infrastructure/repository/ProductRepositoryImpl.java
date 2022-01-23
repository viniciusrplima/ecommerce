package com.pacheco.app.ecommerce.infrastructure.repository;

import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductRepositoryQueries;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryQueries {

    @PersistenceContext
    private EntityManager manager;

    @Override
    @Transactional
    public List<Product> findWithAttrbutes(String query, Long type, Long limit, Long page) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();

        CriteriaQuery criteria = builder.createQuery(Product.class);
        Root<Product> root = criteria.from(Product.class);
        Join<Product, ProductType> joined = root.join("types");

        List<Predicate> predicates = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            predicates.add(builder.like(builder.upper(root.get("name")), ("%" + query + "%").toUpperCase(Locale.ROOT)));
        }

        if (type != null) {
            predicates.add(builder.equal(joined.get("id"), type));
        }

        criteria.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Product> typedQuery = manager.createQuery(criteria);

        if (limit != null) {
            typedQuery.setMaxResults(limit.intValue());
        } else {
            limit = Long.valueOf(10);
        }

        if (page != null) {
            typedQuery.setFirstResult((int) (limit * (page-1)));
        }

        return typedQuery.getResultList();
    }
}
