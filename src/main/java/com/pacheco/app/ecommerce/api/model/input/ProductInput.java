package com.pacheco.app.ecommerce.api.model.input;

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
public class ProductInput {

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

    @NotEmpty
    private List<Long> types;

}
