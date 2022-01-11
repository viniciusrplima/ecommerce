package com.pacheco.app.ecommerce.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ResourceUtil {

    public static String getContentFromResource(String resourceName) {
        try {
            InputStream stream = ResourceUtils.class.getResourceAsStream(resourceName);
            return StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, ?> getContentFromJsonAsMap(String resourceName) throws JsonProcessingException {
        String content = getContentFromResource(resourceName);
        return new ObjectMapper().readValue(content, HashMap.class);
    }

}