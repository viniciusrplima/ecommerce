package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Transactional
    @Query("FROM Cart c " +
            "JOIN FETCH c.customer " +
            "JOIN FETCH c.items ci " +
            "JOIN FETCH ci.product " +
            "WHERE c.customer.email = :email")
    Cart findCartFromUser(String email);
}
