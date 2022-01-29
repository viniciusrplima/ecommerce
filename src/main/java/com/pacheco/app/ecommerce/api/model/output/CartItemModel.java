package com.pacheco.app.ecommerce.api.model.output;

import com.pacheco.app.ecommerce.domain.model.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class CartItemModel {

    private BigInteger quantity;
    private Product product;
}
