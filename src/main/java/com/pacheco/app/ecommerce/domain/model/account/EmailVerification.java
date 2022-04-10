package com.pacheco.app.ecommerce.domain.model.account;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@Table(name = "user_email_verification")
public class EmailVerification {

    @Id
    private String userEmail;

    @Column(nullable = false)
    private String code;

}
