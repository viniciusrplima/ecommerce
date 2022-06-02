package com.pacheco.app.ecommerce.domain.model;

import com.pacheco.app.ecommerce.domain.model.account.Customer;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PurchaseState state;

    @OneToOne
    private Address address;

    @OneToOne
    private Customer customer;
    private BigDecimal shipping;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "purchase")
    private List<PurchaseItem> items;

}
