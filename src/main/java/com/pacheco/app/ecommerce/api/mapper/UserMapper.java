package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.UserInput;
import com.pacheco.app.ecommerce.api.model.output.CustomerModel;
import com.pacheco.app.ecommerce.api.model.output.UserModel;
import com.pacheco.app.ecommerce.domain.model.account.Customer;
import com.pacheco.app.ecommerce.domain.model.account.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public User toUserModel(UserInput userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public UserModel toUserRepresentation(User user) {
        return modelMapper.map(user, UserModel.class);
    }

    public List<UserModel> toUserRepresentationList(List<User> userList) {
        return userList.stream()
                .map(this::toUserRepresentation)
                .collect(Collectors.toList());
    }

    public Customer toCustomerModel(UserInput userDTO) {
        return modelMapper.map(userDTO, Customer.class);
    }

    public CustomerModel toCustomerRepresentation(Customer customer) {
        return modelMapper.map(customer, CustomerModel.class);
    }

}
