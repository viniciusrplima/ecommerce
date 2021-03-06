package com.pacheco.app.ecommerce.domain.service;

import com.pacheco.app.ecommerce.domain.exception.BusinessException;
import com.pacheco.app.ecommerce.domain.exception.PurchaseNotFoundException;
import com.pacheco.app.ecommerce.domain.mapper.CartMapper;
import com.pacheco.app.ecommerce.domain.model.Address;
import com.pacheco.app.ecommerce.domain.model.Cart;
import com.pacheco.app.ecommerce.domain.model.Purchase;
import com.pacheco.app.ecommerce.domain.model.PurchaseItem;
import com.pacheco.app.ecommerce.domain.model.PurchaseState;
import com.pacheco.app.ecommerce.domain.repository.AddressRepository;
import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import com.pacheco.app.ecommerce.domain.repository.CartRepository;
import com.pacheco.app.ecommerce.domain.repository.PurchaseItemRepository;
import com.pacheco.app.ecommerce.domain.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseService {

    @Autowired private ProductService productService;
    @Autowired private UserService userService;
    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private PurchaseItemRepository purchaseItemRepository;
    @Autowired private AddressRepository addressRepository;

    public Purchase confirmPurchase(Address address, String username) {
        Cart cart = cartRepository.findCartFromUser(username)
                .orElseThrow(() -> new BusinessException("Cart is empty"));
        addressRepository.save(address);

        Purchase purchase = Purchase.builder()
                .address(address)
                .customer(userService.getCurrentCustomer())
                .state(PurchaseState.WAITING)
                .build();
        purchase = purchaseRepository.save(purchase);

        List<PurchaseItem> purchaseItemList = CartMapper.toPurchaseItems(cart, purchase);
        purchaseItemRepository.saveAll(purchaseItemList);
        purchase.setItems(purchaseItemList);

        cart.getItems().forEach(ci -> cartItemRepository.delete(ci));
        cartRepository.delete(cart);

        return purchase;
    }

    private Purchase findById(Long purchaseId) {
        return purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseNotFoundException(purchaseId));
    }

    public Purchase findByIdAndEmail(Long purchaseId, String email) {
        return purchaseRepository.findByIdAndEmail(purchaseId, email)
                .orElseThrow(() -> new PurchaseNotFoundException(purchaseId));
    }

    public Purchase confirmPayment(Long purchaseId) {
        Purchase purchase = findById(purchaseId);
        purchase.setState(PurchaseState.PAYED);
        return purchaseRepository.save(purchase);
    }

    public Purchase confirmDeliver(Long purchaseId) {
        Purchase purchase = findById(purchaseId);
        purchase.setState(PurchaseState.DELIVERED);
        return purchaseRepository.save(purchase);
    }

    public void cancelPurchase(Long purchaseId) {
        purchaseRepository.deleteById(purchaseId);
    }
}
