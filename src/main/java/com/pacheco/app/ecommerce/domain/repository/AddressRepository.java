package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.Address;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CustomJpaRepository<Address, Long> {
}
