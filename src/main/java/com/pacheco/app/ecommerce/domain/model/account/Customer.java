package com.pacheco.app.ecommerce.domain.model.account;

import com.pacheco.app.ecommerce.domain.model.Address;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import java.util.List;

//@Entity
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

}
