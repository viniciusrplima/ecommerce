package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.constants.BusinessRules;
import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.OutOfStockException;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.repository.BatchRepository;
import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ProductAllocationService {

    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private BatchRepository batchRepository;

    public Cart addProductToCart(Long productId, String username) {
        Customer customer = userService.findCustomer(username);
        Product product = productService.findById(productId);
        Cart cart = cartRepository.findCartFromUser(username).orElse(cartRepository.save(new Cart(customer)));

        BigInteger stock = batchRepository.countProductsInStock(productId);

        if (BigInteger.ZERO.equals(stock)) {
            throw new OutOfStockException(product.getName());
        }

        CartItem cartItem = cartItemRepository.findCartItemFromCart(productId, username)
                .orElse(CartItem.builder()
                        .quantity(BigInteger.ZERO)
                        .cart(cart)
                        .product(product)
                        .reserved(true)
                        .reservedUntil(
                                LocalDateTime.now().plus(
                                Duration.ofMinutes(BusinessRules.CART_ITEM_RESERVE_MINUTES)))
                        .build());

        cartItem.setQuantity(cartItem.getQuantity().add(BigInteger.ONE));
        cartItem = cartItemRepository.save(cartItem);

        cart.getItems().add(cartItem);

        return cart;
    }

    public Cart removeProductFromCart(Long productId, String username) {
        Customer customer = userService.findCustomer(username);
        Product product = productService.findById(productId);
        CartItem cartItem = cartItemRepository.findCartItemFromCart(productId, username)
                .orElseThrow(() -> new BusinessException(String.format("Product '%s' is not in cart", product.getName())));

        cartItemRepository.delete(cartItem);

        Cart cart = cartRepository.findCartFromUser(username).orElse(new Cart(customer));

        return cart;
    }
}
