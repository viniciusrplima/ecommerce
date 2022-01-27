package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.CartIsEmptyException;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Transactional
    public Cart addProductToCart(Long productId) {
        Customer customer = userService.getCurrentCustomer();
        Product product = productService.getProductFromStock(productId);
        Cart cart = customer.getCart();

        if (cart == null) {
            cart = createEmptyCart(customer);
            cart = cartRepository.save(cart);
            customer.setCart(cart);
        }

        addCartItemToCart(product, cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeProductFromCart(Long productId) {
        Customer customer = userService.getCurrentCustomer();
        Cart cart = customer.getCart();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartIsEmptyException();
        }

        List<CartItem> cartItemsToRemove = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .collect(Collectors.toList());

        if (cartItemsToRemove.isEmpty()) {
            throw new BusinessException(String.format("Product with id %d is not in cart", productId));
        }

        cartItemRepository.deleteAll(cartItemsToRemove);
        cart.getItems().removeAll(cartItemsToRemove);

        return cartRepository.save(cart);
    }

    private Cart createEmptyCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setItems(List.of());
        return cart;
    }

    private CartItem createCartItem(Product product, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        return cartItem;
    }

    private void addCartItemToCart(Product product, Cart cart) {
        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(createCartItem(product, cart));

        cartItem.setQuantity(cartItem.getQuantity().add(BigInteger.ONE));
        cartItem = cartItemRepository.save(cartItem);
        cart.getItems().add(cartItem);
    }
}
