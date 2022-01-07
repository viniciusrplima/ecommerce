package com.pacheco.app.ecommerce.util;

import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.account.Admin;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.Employee;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
@Profile("dev")
public class DummyData {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void setUp() {
        Product product = new Product();
        product.setName("Bola de Basquete");
        product.setPrice(BigDecimal.valueOf(120));

        productRepository.save(product);

        User admin = new Admin();
        admin.setName("João da Silva");
        admin.setEmail("joao.silva@gmail.com");
        admin.setPassword("123");

        User employee = new Employee();
        employee.setName("Sebastião Gonçalvez");
        employee.setEmail("joao.silva@gmail.com");
        employee.setPassword("12345");

        User customer = new Customer();
        customer.setName("Maria das Dores Tenenbaum");
        customer.setEmail("maria.tanenbaum@gmail.com");
        customer.setPassword("abc123");

        userRepository.save(admin);
        userRepository.save(employee);
        userRepository.save(customer);

    }
}
