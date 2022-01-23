package com.pacheco.app.ecommerce.infrastructure.repository;

import com.pacheco.app.ecommerce.domain.repository.CustomJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class CustomJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements CustomJpaRepository<T, ID> {

    private EntityManager entityManager;

    public CustomJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityManager = entityManager;
    }

    @Override
    public List<T> findAllPaginated(Specification<T> spec, Long limit, Long page) {
        return findAllPaginated(List.of(spec), limit, page);
    }

    @Override
    public List<T> findAllPaginated(List<Specification<T>> specs, Long limit, Long page) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(getDomainClass());
        Root<T> root = criteria.from(getDomainClass());

        if (page == null) page = Long.valueOf(1);
        if (limit == null) limit = Long.valueOf(10);

        criteria.where(toPredicates(specs, root, criteria, builder));

        TypedQuery query = entityManager.createQuery(criteria);

        query.setMaxResults(limit.intValue());
        query.setFirstResult((int) ((page-1) * limit));

        return query.getResultList();
    }

    private Predicate[] toPredicates(List<Specification<T>> specs,
                                     Root<T> root,
                                     CriteriaQuery criteria,
                                     CriteriaBuilder builder) {

        return specs.stream()
                .map(spec -> spec.toPredicate(root, criteria, builder))
                .toArray(Predicate[]::new);
    }
}
