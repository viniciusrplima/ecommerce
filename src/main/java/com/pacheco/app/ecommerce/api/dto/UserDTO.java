package com.pacheco.app.ecommerce.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 6, max = 100)
    private String password;
    private String name;
}
