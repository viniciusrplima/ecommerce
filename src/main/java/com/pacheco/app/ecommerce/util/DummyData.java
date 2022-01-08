package com.pacheco.app.ecommerce.util;

import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.model.account.Admin;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.Employee;
import com.pacheco.app.ecommerce.domain.model.account.User;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("dev")
public class DummyData {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @PostConstruct
    public void setUp() {
        ProductType productType1 = new ProductType();
        productType1.setName("Comida");
        productType1.setDescription("Comidas entre carnes, frios e laticínios");

        ProductType productType2 = new ProductType();
        productType2.setName("Artigos Esportivos");
        productType2.setDescription("Bolas, chuteiras, camisas, entre outros");

        productTypeRepository.save(productType1);
        productTypeRepository.save(productType2);

        Product product1 = new Product();
        product1.setName("Bola de Basquete");
        product1.setPrice(BigDecimal.valueOf(120));
        product1.setTypes(List.of(productType2));

        Product product2 = new Product();
        product2.setName("Sorvete");
        product2.setPrice(BigDecimal.valueOf(5));
        product2.setTypes(List.of(productType1));

        Product product3 = new Product();
        product3.setName("Bandeja de Frango");
        product3.setPrice(BigDecimal.valueOf(10));
        product3.setTypes(List.of(productType1));

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

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
