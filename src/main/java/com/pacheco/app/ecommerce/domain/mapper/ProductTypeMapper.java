package com.pacheco.app.ecommerce.domain.mapper;

import com.pacheco.app.ecommerce.api.dto.ProductTypeDTO;
import com.pacheco.app.ecommerce.domain.model.Image;
import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.pacheco.app.ecommerce.domain.mapper.ImageMapper.toImage;
import static com.pacheco.app.ecommerce.util.ImageUtils.encodeToBase64;

public class ProductTypeMapper {

    public static ProductType toProductType(ProductTypeDTO dto, MultipartFile icon) {
        return mergeProductType(new ProductType(), dto, icon);
    }

    public static ProductType mergeProductType(
            ProductType productType, ProductTypeDTO dto, MultipartFile icon) {

        productType.setName(dto.getName());
        productType.setDescription(dto.getDescription());

        if (icon != null) {
            productType.setIcon(toImage(icon));
        }

        return productType;
    }

}
