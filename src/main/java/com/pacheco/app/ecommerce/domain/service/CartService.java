package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.CartIsEmptyException;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import com.pacheco.app.ecommerce.domain.repository.ProductRepository;
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
        Product product = productService.getProductFromStock(productId, BigInteger.ONE);
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
        Product product = productService.findById(productId);
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

        // Remove items from cart and replace them in stock
        cartItemRepository.deleteAll(cartItemsToRemove);
        cart.getItems().removeAll(cartItemsToRemove);
        BigInteger removedCount = BigInteger.ZERO;

        for (CartItem ci : cartItemsToRemove) {
            removedCount = removedCount.add(ci.getQuantity());
        }

        productService.replaceProductInStock(productId, removedCount);

        return cartRepository.save(cart);
    }

    public Cart updateCartItem(Long productId, CartItem cartItem) {
        Customer customer = userService.getCurrentCustomer();
        Product product = productService.findById(productId);
        Cart cart = customer.getCart();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartIsEmptyException();
        }

        CartItem oldCartItem = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(createCartItem(product, cart));

        BigInteger quantityDiff = cartItem.getQuantity().subtract(oldCartItem.getQuantity());

        if (quantityDiff.compareTo(BigInteger.ZERO) < 0) {
            productService.replaceProductInStock(productId, quantityDiff.multiply(BigInteger.valueOf(-1)));
        }
        else if (quantityDiff.compareTo(BigInteger.ZERO) > 0) {
            productService.getProductFromStock(productId, quantityDiff);
        }

        oldCartItem.setQuantity(cartItem.getQuantity());
        cartItemRepository.save(oldCartItem);

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
        cartItem.setQuantity(BigInteger.ZERO);
        return cartItem;
    }

    private void addCartItemToCart(Product product, Cart cart) {
        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(createCartItem(product, cart));

        cartItem.setQuantity(cartItem.getQuantity().add(BigInteger.ONE));
        cartItemRepository.save(cartItem);
    }
}
