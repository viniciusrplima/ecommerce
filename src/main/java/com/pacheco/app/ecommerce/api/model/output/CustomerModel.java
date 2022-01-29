package com.pacheco.app.ecommerce.api.model.output;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerModel extends UserModel {

    private String cpf;
    private String phone;

}
