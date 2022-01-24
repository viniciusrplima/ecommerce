package com.pacheco.app.ecommerce.domain.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@Entity
public class Address extends EmbeddableAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder
    public Address(String state,
                   String city,
                   String cep,
                   String street,
                   String number,
                   String district,
                   String complement,
                   String referencePoint,
                   Long id) {
        super(state, city, cep, street, number, district, complement, referencePoint);
        this.id = id;
    }
}
