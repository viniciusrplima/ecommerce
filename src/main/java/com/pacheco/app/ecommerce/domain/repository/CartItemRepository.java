package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

}
