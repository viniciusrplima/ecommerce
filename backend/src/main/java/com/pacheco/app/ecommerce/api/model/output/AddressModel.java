package com.pacheco.app.ecommerce.api.model.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressModel {

    private String state;
    private String city;
    private String cep;
    private String street;
    private String number;
    private String district;
    private String complement;
    private String referencePoint;

}
