package com.pacheco.app.ecommerce.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigInteger quantity;

    @ManyToOne
    private Product product;

    @OneToOne
    @JsonIgnore
    @Lazy
    private Cart cart;
    private LocalDateTime reservedUntil;
    private Boolean reserved;

    public CartItem(BigInteger quantity, Product product, Cart cart) {
        this.quantity = quantity;
        this.product = product;
        this.cart = cart;
    }
}
