package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.CartItemInput;
import com.pacheco.app.ecommerce.api.model.output.CartItemModel;
import com.pacheco.app.ecommerce.domain.model.CartItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CartItem toModel(CartItemInput cartItemInput) {
        return modelMapper.map(cartItemInput, CartItem.class);
    }

    public CartItemModel toRepresentation(CartItem cartItem) {
        return modelMapper.map(cartItem, CartItemModel.class);
    }

    public List<CartItemModel> toRepresentationList(List<CartItem> cartItemList) {
        return cartItemList.stream()
                .map(this::toRepresentation)
                .collect(Collectors.toList());
    }

}
