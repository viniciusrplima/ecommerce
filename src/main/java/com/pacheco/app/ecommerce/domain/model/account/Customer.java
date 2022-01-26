package com.pacheco.app.ecommerce.domain.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.Cart;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Customer extends User {

    private String cpf;
    private String phone;

    @OneToMany
    @JoinTable(
            name = "customer_address",
            joinColumns = @JoinColumn(name = "customer_email"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private List<Address> addresses;

    @OneToOne(mappedBy = "customer")
    @JsonIgnore
    private Cart cart;

    @Builder(builderMethodName = "customerBuilder")
    public Customer(String email,
                    String password,
                    String name,
                    UserRole role,
                    String cpf,
                    String phone,
                    List<Address> addresses) {
        super(email, password, name, role);
        this.cpf = cpf;
        this.phone = phone;
        this.addresses = addresses;
    }
}
