package com.pacheco.app.ecommerce.domain.mapper;

import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import com.pacheco.app.ecommerce.domain.model.Purchase;
import com.pacheco.app.ecommerce.domain.model.PurchaseItem;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    public static List<PurchaseItem> toPurchaseItems(Cart cart, Purchase purchase) {
        return cart.getItems().stream()
                .map(p -> toPurchaseItem(p, purchase))
                .collect(Collectors.toList());
    }

    public static PurchaseItem toPurchaseItem(CartItem cartItem, Purchase purchase) {
        return PurchaseItem.builder()
                .product(cartItem.getProduct())
                .purchase(purchase)
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getProduct().getPrice())
                .build();
    }
}
