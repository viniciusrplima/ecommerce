package com.pacheco.app.ecommerce.api.model.output;

import com.pacheco.app.ecommerce.domain.model.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductTypeModel {

    private Long id;
    private String name;
    private String description;
    private Image icon;

}
