package com.pacheco.app.ecommerce.util;

import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.repository.AddressRepository;
import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductTypeRepository;
import com.pacheco.app.ecommerce.domain.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Getter
@NoArgsConstructor
public class DataUtil {

    @Autowired private AuthenticationUtil authenticationUtil;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductTypeRepository productTypeRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;

    private List<Product> products;
    private Product samsungProd;
    private Product productOutOfStock;

    private List<ProductType> productTypes;
    private ProductType eletronicos;
    private ProductType withoutDependentProdType;
    private ProductType withDependentProdType;

    private Cart cart;

    private List<Address> addresses;

    public void prepareCarts() {
        prepareProducts();

        Customer customer = authenticationUtil.getCustomer();
        final Cart cart = cartRepository.save(new Cart(customer));
        cart.setCustomer(customer);
        cart.setItems(products.stream()
                .map(product -> new CartItem(BigInteger.valueOf(5), product, cart))
                .map(cartItemRepository::save)
                .collect(Collectors.toList()));

        this.cart = cartRepository.save(cart);
        customer.setCart(this.cart);
        userRepository.save(customer);
    }

    public void prepareProducts() {
        productTypes = new ArrayList<>();

        eletronicos = new ProductType();
        eletronicos.setName("eletronicos");
        eletronicos.setDescription("celulares, TVs, video games, etc");
        productTypes.add(eletronicos);

        withoutDependentProdType = new ProductType();
        withoutDependentProdType.setName("Artigos Esportivos");
        withoutDependentProdType.setDescription("Bolas, Chuteiras, Raquetes entre outros");
        productTypes.add(withoutDependentProdType);

        withDependentProdType = new ProductType();
        withDependentProdType.setName("Artigos Esportivos");
        withDependentProdType.setDescription("Bolas, Chuteiras, Raquetes entre outros");
        productTypes.add(withDependentProdType);

        productTypeRepository.saveAll(productTypes);

        products = new ArrayList<>();

        samsungProd = new Product();
        samsungProd.setName("Samsung J1 Mini");
        samsungProd.setDescription("RAM: 2GB, CPU: 1.5GHz, HD: 16GB");
        samsungProd.setPrice(BigDecimal.valueOf(650));
        samsungProd.setActive(Boolean.TRUE);
        samsungProd.setStock(BigInteger.valueOf(35));
        samsungProd.setTypes(List.of(eletronicos));
        products.add(samsungProd);

        Product product2 = new Product();
        product2.setName("Monark aro 27");
        product2.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        product2.setPrice(BigDecimal.valueOf(895));
        product2.setActive(Boolean.TRUE);
        product2.setStock(BigInteger.valueOf(12));
        product2.setTypes(List.of(withDependentProdType));
        products.add(product2);

        productOutOfStock = new Product();
        productOutOfStock.setName("Monark aro 27");
        productOutOfStock.setDescription("Aro: 27cm, Marcha: sim, Freio a disco: sim");
        productOutOfStock.setPrice(BigDecimal.valueOf(895));
        productOutOfStock.setActive(Boolean.TRUE);
        productOutOfStock.setStock(BigInteger.valueOf(0));
        products.add(productOutOfStock);

        productRepository.saveAll(products);
    }

    public void prepareAddresses() {
        addresses = new ArrayList<>();

        Address address1 = Address.entityBuilder()
                .state("PB")
                .city("Campina Grande")
                .cep("58699-222")
                .district("Dinamerica")
                .street("Floriano Peixoto")
                .number("54G")
                .complement("APT O203")
                .build();
        addresses.add(address1);

        Address address2 = Address.entityBuilder()
                .state("PB")
                .city("Campina Grande")
                .cep("58699-222")
                .district("Dinamerica")
                .street("Floriano Peixoto")
                .number("54G")
                .complement("APT O203")
                .build();
        addresses.add(address2);

        addressRepository.saveAll(addresses);

        Customer customer = authenticationUtil.getCustomer();
        customer.setAddresses(addresses);

        userRepository.save(customer);
    }

}
