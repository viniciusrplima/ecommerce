package com.pacheco.app.ecommerce.api.model.output;

import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemModel {

    private BigInteger quantity;
    private BigDecimal unitPrice;
    private ProductModel product;

}
