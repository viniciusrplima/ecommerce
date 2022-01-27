package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import com.pacheco.app.ecommerce.domain.service.CartService;
import com.pacheco.app.ecommerce.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.CART)
public class CartController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @GetMapping
    public Cart getCartItems() {
        return cartRepository.findCartFromUser(userService.getCurrentUsername());
    }

    @PostMapping("/product/{productId}")
    public Cart addProductToCart(@PathVariable Long productId) {
        return cartService.addProductToCart(productId);
    }

    @DeleteMapping("/product/{productId}")
    public Cart removeProductFromCart(@PathVariable Long productId) {
        return cartService.removeProductFromCart(productId);
    }
}
