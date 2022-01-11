package com.pacheco.app.ecommerce.domain.mapper;

import com.pacheco.app.ecommerce.domain.exception.CouldNotOpenImageException;
import com.pacheco.app.ecommerce.domain.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.pacheco.app.ecommerce.util.ImageUtils.encodeToBase64;

public class ImageMapper {

    public static Image toImage(MultipartFile file) {
        try {
            return new Image(encodeToBase64(file));
        } catch (IOException e) {
            throw new CouldNotOpenImageException(e);
        }
    }
}
