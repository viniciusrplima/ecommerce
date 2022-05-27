package com.pacheco.app.ecommerce.api.model.output;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
public class BatchModel {

    private Long id;
    private BigInteger quantity;
    private LocalDateTime registeredAt;
    private ProductModel product;

}
