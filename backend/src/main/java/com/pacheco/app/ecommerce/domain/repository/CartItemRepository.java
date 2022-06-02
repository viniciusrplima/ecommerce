package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci " +
            "FROM CartItem ci " +
            "JOIN ci.product p " +
            "JOIN ci.cart.customer u " +
            "WHERE p.id = :productId " +
            "AND u.email = :username")
    public Optional<CartItem> findCartItemFromCart(Long productId, String username);


    @Modifying
    @Query("UPDATE CartItem ci " +
            "SET ci.reserved = false " +
            "WHERE ci.reservedUntil < CURRENT_TIMESTAMP " +
            "AND ci.reserved = true")
    @Transactional
    public Object updateExpiredCartItems();
}
