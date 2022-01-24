package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.ProductTypeInput;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import org.springframework.web.multipart.MultipartFile;

import static com.pacheco.app.ecommerce.api.mapper.ImageMapper.toImage;

public class ProductTypeMapper {

    public static ProductType toProductType(ProductTypeInput dto, MultipartFile icon) {
        return mergeProductType(new ProductType(), dto, icon);
    }

    public static ProductType mergeProductType(
            ProductType productType, ProductTypeInput dto, MultipartFile icon) {

        productType.setName(dto.getName());
        productType.setDescription(dto.getDescription());

        if (icon != null) {
            productType.setIcon(toImage(icon));
        }

        return productType;
    }

}
