package com.pacheco.app.ecommerce.api.dto;

import com.pacheco.app.ecommerce.core.validation.Groups;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.pacheco.app.ecommerce.core.validation.ValidationPatterns.CPF;
import static com.pacheco.app.ecommerce.core.validation.ValidationPatterns.PHONE_NUMBER;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 6, max = 100)
    private String password;
    private String name;

    @NotBlank(groups = Groups.UserRole.class)
    private String role;

    @NotBlank(groups = Groups.ConsumerInfo.class)
    @Pattern(regexp = CPF, groups = Groups.ConsumerInfo.class)
    private String cpf;

    @NotBlank(groups = Groups.ConsumerInfo.class)
    @Pattern(regexp = PHONE_NUMBER, groups = Groups.ConsumerInfo.class)
    private String phone;

}
