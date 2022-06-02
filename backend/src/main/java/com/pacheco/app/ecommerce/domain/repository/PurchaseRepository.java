package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("FROM Purchase p " +
            "JOIN FETCH p.customer c " +
            "WHERE p.id = :id " +
            "AND c.email = :email")
    public Optional<Purchase> findByIdAndEmail(Long id, String email);

    @Query("FROM Purchase p " +
            "JOIN FETCH p.customer c " +
            "WHERE c.email = :email")
    public List<Purchase> findAllPurchases(String email);

}
