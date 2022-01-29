package com.pacheco.app.ecommerce.api.model.output;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserModel {

    private String email;
    private String name;
    private String role;

}
