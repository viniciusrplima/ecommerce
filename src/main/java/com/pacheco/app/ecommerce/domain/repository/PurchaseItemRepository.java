package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
}
