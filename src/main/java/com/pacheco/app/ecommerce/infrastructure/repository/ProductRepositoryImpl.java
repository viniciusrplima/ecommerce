package com.pacheco.app.ecommerce.infrastructure.repository;

import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductRepositoryQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery(Product.class);
        Root<Product> root = criteria.from(Product.class);

        List<Specification> specs = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            specs.add(withLikeName(query));
        }

        if (type != null) {
            specs.add(withType(type));
        }

        criteria.where(toPredicates(specs, root, criteria, builder));

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

    private Predicate[] toPredicates(List<Specification> specs,
                                     Root<Product> root,
                                     CriteriaQuery criteria,
                                     CriteriaBuilder builder) {

        return specs.stream()
                .map(spec -> spec.toPredicate(root, criteria, builder))
                .toArray(Predicate[]::new);
    }

}
