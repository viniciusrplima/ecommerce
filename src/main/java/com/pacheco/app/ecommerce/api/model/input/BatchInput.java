package com.pacheco.app.ecommerce.api.model.input;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class BatchInput {

    @NotNull
    @PositiveOrZero
    private BigInteger quantity;

    @NotNull
    private ProductRef product;

}
