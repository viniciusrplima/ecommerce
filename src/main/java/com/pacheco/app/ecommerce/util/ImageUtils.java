package com.pacheco.app.ecommerce.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageUtils {

    public static String encodeToBase64(MultipartFile file) throws IOException {
        String base64Image = "";
        base64Image += String.format("data:%s;base64,", file.getContentType());
        base64Image += StringUtils.newStringUtf8(Base64.encodeBase64(file.getBytes()));
        return base64Image;
    }

}
