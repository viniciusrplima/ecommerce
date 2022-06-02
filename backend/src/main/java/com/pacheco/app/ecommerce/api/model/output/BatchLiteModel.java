package com.pacheco.app.ecommerce.api.model.output;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BatchLiteModel {

    private Long id;
    private BigInteger quantity;
    private LocalDateTime registeredAt;
    private String productName;

}
