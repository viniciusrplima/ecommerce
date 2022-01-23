package com.pacheco.app.ecommerce.api.dto;

import com.pacheco.app.ecommerce.domain.model.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {

    @NotBlank
    @Length(min = 3, max = 30)
    private String name;

    @NotBlank
    @Length(min = 5, max = 255)
    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private BigInteger stock;
    private Boolean active;
    private List<Long> types;

}
