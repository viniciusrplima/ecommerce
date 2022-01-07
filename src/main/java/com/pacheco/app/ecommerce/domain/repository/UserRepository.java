package com.pacheco.app.ecommerce.domain.repository;

import com.pacheco.app.ecommerce.domain.model.account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

}
