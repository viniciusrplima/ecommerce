package com.pacheco.app.ecommerce.api.model.output;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductModel {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigInteger stock;
    private Boolean active;
    private List<ProductTypeModel> types;

}
