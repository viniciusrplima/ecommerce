package com.pacheco.app.ecommerce.api.model.input;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.index.qual.Positive;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class CartItemInput {

    @Positive
    private BigInteger quantity;
}
