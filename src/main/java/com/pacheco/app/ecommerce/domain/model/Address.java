package com.pacheco.app.ecommerce.domain.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String state;
    private String city;
    private String cep;
    private String street;
    private String number;
    private String district;
    private String complement;
    private String referencePoint;

}
