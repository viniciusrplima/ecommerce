package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.mapper.CartMapper;
import com.pacheco.app.ecommerce.api.model.output.CartItemModel;
import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.ProductNotFoundException;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import com.pacheco.app.ecommerce.domain.service.ProductAllocationService;
import com.pacheco.app.ecommerce.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping(Routes.CART)
public class CartController {

    @Autowired private UserService userService;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartMapper cartMapper;
    @Autowired private ProductAllocationService productAllocationService;

    @GetMapping("/total")
    public BigInteger getNumItemsInCart() {
        return cartRepository.countTotalCartItems(userService.getCurrentUsername());
    }

    @GetMapping
    public List<CartItemModel> getCartItems() {
        return cartMapper.toRepresentationList(cartRepository
                .findCartFromUser(userService.getCurrentUsername())
                .orElse(new Cart())
                .getItems());
    }

    @PostMapping("/product/{productId}")
    public List<CartItemModel> addProductToCart(@PathVariable Long productId) {
        return cartMapper.toRepresentationList(
                productAllocationService.addProductToCart(productId, userService.getCurrentUsername()).getItems());
    }

    @DeleteMapping("/product/{productId}")
    public List<CartItemModel> removeProductFromCart(@PathVariable Long productId) {
        try {
            return cartMapper.toRepresentationList(
                    productAllocationService.removeProductFromCart(productId, userService.getCurrentUsername()).getItems());
        } catch (ProductNotFoundException e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }
}
