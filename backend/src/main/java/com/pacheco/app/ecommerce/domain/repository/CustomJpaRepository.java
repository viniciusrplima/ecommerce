package com.pacheco.app.ecommerce.domain.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    public List<T> findAllPaginated(Specification<T> spec, Long limit, Long page);
    public List<T> findAllPaginated(List<Specification<T>> specs, Long limit, Long page);
    public Long countAll(List<Specification<T>> specs);
}
