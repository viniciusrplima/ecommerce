package com.pacheco.app.ecommerce.api.model.output;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchProductModel {

    private Long count;
    private List<ProductModel> products;

}
