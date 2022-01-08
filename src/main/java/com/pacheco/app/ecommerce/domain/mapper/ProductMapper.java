package com.pacheco.app.ecommerce.domain.mapper;

import com.pacheco.app.ecommerce.api.dto.ProductDTO;
import com.pacheco.app.ecommerce.domain.model.Image;
import com.pacheco.app.ecommerce.domain.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

import static com.pacheco.app.ecommerce.util.ImageUtils.encodeToBase64;

public class ProductMapper {

    public static Product toProduct(ProductDTO dto, MultipartFile photo) throws IOException {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock() != null ? dto.getStock() : BigInteger.ZERO);
        product.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
        product.setTypes(dto.getTypes());

        if (photo != null) {
            product.setPhoto(new Image(encodeToBase64(photo)));
        }

        return product;
    }

}
