package com.pacheco.app.ecommerce.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.pacheco.app.ecommerce.core.validation.ValidationPatterns.CEP;

@NoArgsConstructor
@Getter
@Setter
public class AddressDTO {

    @NotBlank
    private String state;

    @NotBlank
    private String city;

    @NotBlank
    @Pattern(regexp = CEP)
    private String cep;

    @NotBlank
    private String street;

    @NotBlank
    private String number;

    @NotBlank
    private String district;

    private String complement;
    private String referencePoint;

}
