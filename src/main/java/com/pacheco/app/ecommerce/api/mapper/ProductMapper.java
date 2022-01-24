package com.pacheco.app.ecommerce.api.mapper;

import com.pacheco.app.ecommerce.api.model.input.ProductInput;
import com.pacheco.app.ecommerce.domain.model.Image;
import com.pacheco.app.ecommerce.domain.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

import static com.pacheco.app.ecommerce.util.ImageUtils.encodeToBase64;

public class ProductMapper {

    public static Product toProduct(ProductInput dto, MultipartFile photo) throws IOException {
        return mergeProduct(new Product(), dto, photo);
    }

    public static Product mergeProduct(Product product, ProductInput dto, MultipartFile photo) throws IOException {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock() != null ? dto.getStock() : BigInteger.ZERO);
        product.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);

        if (photo != null) {
            product.setPhoto(new Image(encodeToBase64(photo)));
        }

        return product;
    }

}
