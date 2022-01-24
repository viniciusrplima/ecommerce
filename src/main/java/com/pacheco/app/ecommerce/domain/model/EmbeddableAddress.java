package com.pacheco.app.ecommerce.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbeddableAddress {

    @Column(name = "address_state")
    private String state;

    @Column(name = "address_city")
    private String city;

    @Column(name = "address_cep")
    private String cep;

    @Column(name = "address_street")
    private String street;

    @Column(name = "address_number")
    private String number;

    @Column(name = "address_district")
    private String district;

    @Column(name = "address_complement")
    private String complement;

    @Column(name = "address_reference_point")
    private String referencePoint;

}
