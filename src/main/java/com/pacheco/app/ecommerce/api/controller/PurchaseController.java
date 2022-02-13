package com.pacheco.app.ecommerce.api.controller;

import com.pacheco.app.ecommerce.api.mapper.AddressMapper;
import com.pacheco.app.ecommerce.api.mapper.PurchaseMapper;
import com.pacheco.app.ecommerce.api.model.input.AddressInput;
import com.pacheco.app.ecommerce.api.model.output.PurchaseModel;
import com.pacheco.app.ecommerce.domain.repository.PurchaseRepository;
import com.pacheco.app.ecommerce.domain.service.PurchaseService;
import com.pacheco.app.ecommerce.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(Routes.PURCHASES)
public class PurchaseController {

    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private PurchaseService purchaseService;
    @Autowired private PurchaseMapper purchaseMapper;
    @Autowired private UserService userService;
    @Autowired private AddressMapper addressMapper;

    @GetMapping
    public List<PurchaseModel> getPurchases() {
        return purchaseMapper.toRepresentationList(
                purchaseRepository.findAllPurchases(userService.getCurrentUsername()));
    }

    @GetMapping("/{purchaseId}")
    public PurchaseModel getPurchase(@PathVariable Long purchaseId) {
        return purchaseMapper.toRepresentation(
                purchaseService.findByIdAndEmail(purchaseId, userService.getCurrentUsername()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseModel confirmPurchase(@RequestBody @Valid AddressInput address) {
        return purchaseMapper.toRepresentation(
                purchaseService.confirmPurchase(addressMapper.toModelEmbeddable(address)));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping("/{purchaseId}")
    public void cancelPurchase(@PathVariable Long purchaseId) {
        purchaseService.cancelPurchase(purchaseId);
    }
}
