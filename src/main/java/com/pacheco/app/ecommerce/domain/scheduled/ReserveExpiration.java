package com.pacheco.app.ecommerce.domain.scheduled;

import com.pacheco.app.ecommerce.domain.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReserveExpiration {

    private static final int UPDATE_RATE_SECS = 60;

    @Autowired private CartItemRepository cartItemRepository;

    @Scheduled(fixedRate = UPDATE_RATE_SECS * 1000)
    public void updateCartReserveExpiration() {
        System.out.println("reserve");
    }

}
