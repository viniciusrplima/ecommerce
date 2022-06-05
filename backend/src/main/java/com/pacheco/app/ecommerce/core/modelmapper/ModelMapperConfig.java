package com.pacheco.app.ecommerce.core.modelmapper;

import com.pacheco.app.ecommerce.domain.model.account.UserRole;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();

        mm.createTypeMap(String.class, UserRole.class).setConverter(createEnumConverter(UserRole.class));

        return mm;
    }

    private static <T extends Enum<T>> Converter<String, T> createEnumConverter(Class<T> typeClass) {
        return ctx -> ctx.getSource() == null ? null : T.valueOf(typeClass, ctx.getSource().toUpperCase());
    }
}
