package com.pacheco.app.ecommerce.infrastructure.repository.spec;

import com.pacheco.app.ecommerce.domain.model.Product;
import com.pacheco.app.ecommerce.domain.model.ProductType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;


public class ProductSpecs {

    public static Specification<Product> withLikeName(String name) {
        return (root, query, builder) ->
                builder.like(builder.upper(root.get("name")), ("%" + name + "%").toUpperCase());
    }

    public static Specification<Product> withType(Long typeId) {
        return (root, query, builder) -> {
            Join<Product, ProductType> joined = root.join("types");
            return builder.equal(joined.get("id"), typeId);
        };
    }

}
