package com.pacheco.app.ecommerce.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDTO {

    @NotBlank
    @Length(min = 3, max = 30)
    private String name;

    @NotBlank
    @Length(min = 5, max = 255)
    private String description;

}
