package com.pacheco.app.ecommerce.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigInteger quantity;

    @CreationTimestamp
    private LocalDateTime registeredAt;

    @ManyToOne
    private Product product;

}
