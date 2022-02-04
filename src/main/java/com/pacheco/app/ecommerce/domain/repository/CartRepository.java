package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Transactional
    @Query("FROM Cart c " +
            "JOIN FETCH c.customer " +
            "JOIN FETCH c.items ci " +
            "JOIN FETCH ci.product " +
            "WHERE c.customer.email = :email")
    Optional<Cart> findCartFromUser(String email);

    @Transactional
    @Query("SELECT SUM(ci.quantity) " +
            "FROM Cart c " +
            "JOIN c.customer " +
            "JOIN c.items ci " +
            "WHERE c.customer.email = :email")
    BigInteger countTotalCartItems(String email);
}
