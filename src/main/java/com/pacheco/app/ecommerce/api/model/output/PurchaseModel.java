package com.pacheco.app.ecommerce.api.model.output;

import com.pacheco.app.ecommerce.domain.model.PurchaseState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseModel {

    private Long id;
    private PurchaseState state;
    private AddressModel address;
    private LocalDateTime createdAt;
    private List<PurchaseItemModel> items;

}
