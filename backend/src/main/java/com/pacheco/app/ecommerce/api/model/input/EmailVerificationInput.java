package com.pacheco.app.ecommerce.api.model.input;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationInput {

    private String email;
    private String code;

}
